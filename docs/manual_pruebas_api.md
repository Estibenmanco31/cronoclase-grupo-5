# 📖 Manual de Pruebas API: Cronoclase (Modelo Simplificado)

Este manual documenta el flujo estructurado para probar la API de Cronoclase de acuerdo al **nuevo modelo simplificado de 6 entidades**. Con esta arquitectura, se han eliminado las entidades intermedias redundantes (`Curso`, `Matricula`, `Etiqueta`) y se ha implementado una relación directa `@ManyToMany` entre `Grupo` y `Estudiante` gestionada por una tabla de unión automática (`grupo_estudiante`).

Para poder probar correctamente la lógica de inscripciones, evaluaciones, entregas a tiempo/tarde, calificaciones, y el cálculo de la nota final ponderada, debes seguir **estrictamente este orden de creación y consumo**:

---

## 🔗 Enlaces y Utilidades de Documentación

| Herramienta | URL Local |
|---|---|
| **Documentación Interactiva (Scalar)** | `http://localhost:8080/docs` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **Especificación OpenAPI (JSON para importar)** | `http://localhost:8080/v3/api-docs` |

> [!TIP]
> **Importar en Postman / Insomnia**: Puedes copiar la URL del JSON de OpenAPI (`http://localhost:8080/v3/api-docs`) e importarla en tu cliente REST para cargar al instante toda la colección estructurada de endpoints con sus respectivos cuerpos JSON de ejemplo.

---

## 📋 Resumen del Flujo de Pruebas

```
1. Crear Profesor  →  2. Crear Grupo  →  3. Crear Estudiante  →  4. Inscribir Estudiante
→  5. Crear Evaluación (Tope 100%)  →  6. Realizar Entrega (A tiempo / Tarde / Sobrescribir)
→  7. Calificar Entrega (Nota 0.0 - 5.0)  →  8. Calcular Nota Final Ponderada
```

---

## 🟢 FASE 1: Entidades Independientes y Registro de Usuarios

Estas entidades se crean inicialmente en la base de datos sin depender de otras relaciones complejas.

### 1. Registrar un Profesor
Crea el perfil del profesor que gestionará las asignaturas y grupos.
- **Método**: `POST`
- **Endpoint**: `/api/profesor`
- **Body (JSON)**:
```json
{
  "nombre": "Carlos Mendoza",
  "email": "carlos@cronoclase.com",
  "documentoID": "CC-12345678",
  "password": "prof123",
  "activo": true,
  "telefono": "3001234567",
  "direccion": "Calle 45 #12-30",
  "biografia": "Ingeniero de Sistemas con 10 años de experiencia docente",
  "oficina": "Bloque A, Oficina 203",
  "especialidad": "Bases de Datos"
}
```
*Anota el `id` devuelto en la respuesta (ej. `1`).*

### 2. Login del Profesor
Simula la autenticación en la plataforma para comprobar que las credenciales son correctas.
- **Método**: `POST`
- **Endpoint**: `/api/profesor/login`
- **Body (JSON)**:
```json
{
  "email": "carlos@cronoclase.com",
  "password": "prof123"
}
```

### 3. Registrar un Estudiante
Crea al estudiante que participará en las clases y enviará las tareas.
- **Método**: `POST`
- **Endpoint**: `/api/estudiante`
- **Body (JSON)**:
```json
{
  "nombre": "Ana García",
  "email": "ana@estudiante.com",
  "documentoID": "TI-98765432",
  "password": "est123",
  "telefono": "3109876543",
  "direccion": "Carrera 20 #15-40"
}
```
*Anota el `id` devuelto en la respuesta (ej. `1`).*

---

## 🟡 FASE 2: Creación de Grupos e Inscripción Directa

En esta fase vinculamos a los profesores y a los estudiantes a través del objeto `Grupo` (reemplazando el antiguo modelo de `Curso` y `Matricula`).

### 4. Crear un Grupo
Crea la clase asignada al profesor. El nombre del curso se integra directamente en el grupo.
- **Método**: `POST`
- **Endpoint**: `/api/grupo`
- **Body (JSON)** *(Reemplaza `profesor.id` con el ID obtenido en el Paso 1)*:
```json
{
  "nombre": "Bases de Datos - Grupo A",
  "dia": "LUNES",
  "profesor": {
    "id": 1
  }
}
```
> **Días válidos**: `LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`  
*Anota el `id` del grupo devuelto (ej. `1`).*

### 5. Inscribir al Estudiante en el Grupo
Inscribe directamente a un estudiante a la lista del grupo. Puedes hacerlo de dos formas (vía URL o vía JSON):

**Opción A: Vía URL (PathVariables)**
- **Método**: `POST`
- **Endpoint**: `/api/grupo/{grupoId}/inscribir/{estudianteId}`
- **Ejemplo**: `POST http://localhost:8080/api/grupo/1/inscribir/1`
- **Body**: *Vacío*

**Opción B: Vía Body (JSON)**
- **Método**: `POST`
- **Endpoint**: `/api/grupo/inscribir`
- **Ejemplo**: `POST http://localhost:8080/api/grupo/inscribir`
- **Body (JSON)**:
```json
{
  "grupoId": 1,
  "estudianteId": 1
}
```

> [!IMPORTANT]
> **Control de duplicados**: Si intentas realizar esta misma petición dos veces para el mismo estudiante en el mismo grupo (por cualquiera de las dos vías), la API te responderá con un error `400 Bad Request` indicando que el estudiante ya se encuentra matriculado.

---

## 🔴 FASE 3: Evaluaciones, Entregas y Calificaciones (Reglas de Negocio)

Aquí se evalúan las principales restricciones lógicas del negocio escolar.

### 6. Crear una Evaluación
El profesor crea una actividad calificable para su grupo.
- **Método**: `POST`
- **Endpoint**: `/api/evaluacion`
- **Body (JSON)**:
```json
{
  "titulo": "Parcial 1 — Modelo Entidad-Relación",
  "descripcion": "Diseñar el modelo ER para una aplicación de biblioteca",
  "tipo": "PARCIAL",
  "porcentaje": 30.0,
  "fechaEntrega": "2026-06-15",
  "grupoId": 1
}
```
> **Tipos válidos**: `TAREA`, `PARCIAL`, `QUIZ`, `PROYECTO`, `TALLER`, `EXAMEN_FINAL`

> [!WARNING]
> **Regla de negocio (Tope 100%)**: La suma de los porcentajes de todas las evaluaciones en un mismo grupo no puede exceder el `100.0%`. Si creas una evaluación que supere esta suma, la API lanzará un error `400 Bad Request` indicando el porcentaje actual acumulado.

*Anota el `id` de la evaluación devuelta (ej. `1`).*

### 7. Realizar una Entrega (Estudiante)
El estudiante envía su respuesta o la url de su trabajo.
- **Método**: `POST`
- **Endpoint**: `/api/entrega`
- **Body (JSON)**:
```json
{
  "fechaEntregaReal": "2026-06-14",
  "archivoUrl": "https://github.com/ana/parcial1",
  "comentario": "Repositorio con el diagrama y el script SQL",
  "estudianteId": 1,
  "evaluacionId": 1
}
```

> [!NOTE]
> **Cálculo de Estados Automáticos**: 
> - **ENTREGADO**: Si se envía antes o en el mismo día de la `fechaEntrega` de la Evaluación.
> - **TARDE**: Si la fecha actual o `fechaEntregaReal` es posterior a la límite.
> - **PENDIENTE**: Si no se ha enviado y la fecha de entrega aún no expira.
> - **CALIFICADO**: Pasa a este estado una vez que el profesor le asigna una nota.

> **Regla de Sobrescritura**: Si el estudiante realiza otro `POST` a `/api/entrega` con el mismo `estudianteId` y `evaluacionId`, la API **actualizará** la entrega existente en lugar de crear una nueva, evitando duplicar registros.

---

## 🔵 FASE 4: Calificación y Promedio Ponderado

### 8. Calificar la Entrega (Profesor)
El profesor califica el trabajo enviado asignándole una puntuación. Puedes hacerlo de dos formas (vía URL o vía JSON):

**Opción A: Vía URL (Query Parameter)**
- **Método**: `PATCH`
- **Endpoint**: `/api/entrega/{entregaId}/calificar?nota={valor}`
- **Ejemplo**: `PATCH http://localhost:8080/api/entrega/1/calificar?nota=4.5`

**Opción B: Vía Body (JSON)**
- **Método**: `PATCH`
- **Endpoint**: `/api/entrega/{entregaId}/calificar`
- **Ejemplo**: `PATCH http://localhost:8080/api/entrega/1/calificar`
- **Body (JSON)**:
```json
{
  "nota": 4.5
}
```

> [!IMPORTANT]
> **Rango de Notas**: La nota asignada (ya sea por query param o body JSON) debe estar obligatoriamente entre `0.0` y `5.0` (inclusive). Valores fuera de este rango devolverán un error `400 Bad Request`.

### 9. Consultar la Nota Final del Estudiante
Devuelve la nota final ponderada de un estudiante específico en un determinado grupo. Puedes hacerlo de dos formas (vía URL o vía JSON):

**Opción A: Vía URL (PathVariables)**
- **Método**: `GET`
- **Endpoint**: `/api/grupo/{grupoId}/estudiante/{estudianteId}/nota-final`
- **Ejemplo**: `GET http://localhost:8080/api/grupo/1/estudiante/1/nota-final`

**Opción B: Vía Body (JSON)**
- **Método**: `POST`
- **Endpoint**: `/api/grupo/nota-final`
- **Ejemplo**: `POST http://localhost:8080/api/grupo/nota-final`
- **Body (JSON)**:
```json
{
  "grupoId": 1,
  "estudianteId": 1
}
```

**Respuesta esperada (JSON - misma para ambas opciones)**:
```json
{
  "grupoId": 1,
  "estudianteId": 1,
  "notaFinal": 1.35
}
```
*(Cálculo: `4.5` (nota de la entrega) × `30%` (porcentaje de la evaluación) / `100` = `1.35`). Las evaluaciones no entregadas o no calificadas aportan un valor de `0.0` al promedio final.*
