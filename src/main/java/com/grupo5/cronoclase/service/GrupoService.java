package com.grupo5.cronoclase.service;

import com.grupo5.cronoclase.exception.BusinessException;
import com.grupo5.cronoclase.exception.ResourceNotFoundException;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final EstudianteRepository estudianteRepository;
    private final ProfesorRepository profesorRepository;

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public Grupo crearGrupo(Grupo grupo) {
        grupo.setId(null);
        validarProfesorExiste(grupo);
        return grupoRepository.save(grupo);
    }

    public List<Grupo> obtenerTodos() {
        return grupoRepository.findAll();
    }

    public Grupo obtenerPorId(Long id) {
        return grupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado con ID: " + id));
    }

    public List<Grupo> obtenerPorNombre(String nombre) {
        return grupoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Grupos asignados a un profesor específico
    public List<Grupo> obtenerPorProfesor(Long profesorId) {
        if (!profesorRepository.existsById(profesorId)) {
            throw new ResourceNotFoundException("Profesor no encontrado con ID: " + profesorId);
        }
        return grupoRepository.findByProfesorId(profesorId);
    }

    // Grupos en los que está inscrito un estudiante
    public List<Grupo> obtenerPorEstudiante(Long estudianteId) {
        if (!estudianteRepository.existsById(estudianteId)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId);
        }
        return grupoRepository.findByEstudianteId(estudianteId);
    }

    @Transactional
    public Grupo actualizarGrupo(Long id, Grupo datosNuevos) {
        Grupo grupoExistente = obtenerPorId(id);
        grupoExistente.setNombre(datosNuevos.getNombre());
        grupoExistente.setDia(datosNuevos.getDia());
        if (datosNuevos.getProfesor() != null) {
            validarProfesorExiste(datosNuevos);
            grupoExistente.setProfesor(datosNuevos.getProfesor());
        }
        return grupoRepository.save(grupoExistente);
    }

    @Transactional
    public void eliminarGrupo(Long id) {
        obtenerPorId(id);
        grupoRepository.deleteById(id);
    }

    // ─── INSCRIPCIÓN DE ESTUDIANTES ──────────────────────────────────────────

    /**
     * Inscribe un estudiante en un grupo.
     * Regla: Un estudiante no puede estar inscrito dos veces en el mismo grupo.
     */
    @Transactional
    public Grupo inscribirEstudiante(Long grupoId, Long estudianteId) {
        Grupo grupo = obtenerPorId(grupoId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId));

        // Validar que no esté ya inscrito
        boolean yaInscrito = grupoRepository.existsEstudianteInGrupo(grupoId, estudianteId);
        if (yaInscrito) {
            throw new BusinessException("El estudiante ya está inscrito en este grupo.");
        }

        grupo.getEstudiantes().add(estudiante);
        return grupoRepository.save(grupo);
    }

    /**
     * Elimina la inscripción de un estudiante en un grupo.
     */
    @Transactional
    public Grupo desinscribirEstudiante(Long grupoId, Long estudianteId) {
        Grupo grupo = obtenerPorId(grupoId);

        boolean existeInscripcion = grupoRepository.existsEstudianteInGrupo(grupoId, estudianteId);
        if (!existeInscripcion) {
            throw new BusinessException("El estudiante no está inscrito en este grupo.");
        }

        grupo.getEstudiantes().removeIf(e -> e.getId().equals(estudianteId));
        return grupoRepository.save(grupo);
    }

    // ─── CÁLCULO DE NOTA FINAL ───────────────────────────────────────────────

    /**
     * Calcula la nota final ponderada de un estudiante en un grupo.
     * Fórmula: Σ (nota_entrega × porcentaje_evaluacion / 100)
     * Si no hay entrega o no está calificada, su aporte es 0.0.
     */
    public Double calcularNotaFinal(Long grupoId, Long estudianteId) {
        Grupo grupo = obtenerPorId(grupoId);

        if (!estudianteRepository.existsById(estudianteId)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId);
        }

        boolean inscrito = grupoRepository.existsEstudianteInGrupo(grupoId, estudianteId);
        if (!inscrito) {
            throw new BusinessException("El estudiante no está inscrito en este grupo.");
        }

        List<Evaluacion> evaluaciones = grupo.getEvaluaciones();
        if (evaluaciones == null || evaluaciones.isEmpty()) {
            return 0.0;
        }

        double notaFinal = 0.0;
        for (Evaluacion eval : evaluaciones) {
            double nota = 0.0;
            if (eval.getEntregas() != null) {
                Optional<Entrega> entrega = eval.getEntregas().stream()
                        .filter(e -> e.getEstudiante().getId().equals(estudianteId))
                        .findFirst();
                if (entrega.isPresent() && entrega.get().getNota() != null) {
                    nota = entrega.get().getNota();
                }
            }
            notaFinal += nota * (eval.getPorcentaje() / 100.0);
        }

        return Math.round(notaFinal * 100.0) / 100.0;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void validarProfesorExiste(Grupo grupo) {
        if (grupo.getProfesor() == null || grupo.getProfesor().getId() == null) {
            throw new BusinessException("El grupo debe tener un profesor asignado.");
        }
        if (!profesorRepository.existsById(grupo.getProfesor().getId())) {
            throw new ResourceNotFoundException("Profesor no encontrado con ID: " + grupo.getProfesor().getId());
        }
    }
}
