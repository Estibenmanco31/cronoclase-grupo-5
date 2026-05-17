# --- ETAPA 1: Construcción (Build) ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Copiar configuración de Maven y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copiar código fuente y compilar el proyecto Cronoclase
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 3. Copiar el JAR generado (Nombre actualizado a cronoclase)
COPY --from=build /app/target/cronoclase-0.0.1-SNAPSHOT.jar app.jar

# 4. Configuración para el puerto dinámico (Ideal para Render/Railway)
ENV PORT=8080
EXPOSE 8080

# 5. Ejecución con expansión de variable de puerto
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]