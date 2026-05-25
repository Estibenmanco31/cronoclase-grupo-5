package com.grupo5.cronoclase.service;

import com.grupo5.cronoclase.exception.BusinessException;
import com.grupo5.cronoclase.exception.ResourceNotFoundException;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final GrupoRepository grupoRepository;

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public Evaluacion crearEvaluacion(Evaluacion evaluacion) {
        evaluacion.setId(null);
        validarGrupoYPorcentaje(evaluacion.getGrupo().getId(), null, evaluacion.getPorcentaje());
        return evaluacionRepository.save(evaluacion);
    }

    public List<Evaluacion> obtenerTodas() {
        return evaluacionRepository.findAll();
    }

    public Evaluacion obtenerPorId(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
    }

    // Panel del ESTUDIANTE: ver evaluaciones de su grupo
    public List<Evaluacion> obtenerPorGrupo(Long grupoId) {
        if (!grupoRepository.existsById(grupoId)) {
            throw new ResourceNotFoundException("Grupo no encontrado con ID: " + grupoId);
        }
        return evaluacionRepository.findByGrupoId(grupoId);
    }

    // Panel del PROFESOR: buscar evaluaciones por nombre de grupo
    public List<Evaluacion> buscarPorNombreGrupo(String nombre) {
        return evaluacionRepository.findByGrupoNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public Evaluacion actualizarEvaluacion(Long id, Evaluacion datosNuevos) {
        Evaluacion existente = obtenerPorId(id);
        Long grupoId = existente.getGrupo().getId();

        // Validar porcentaje solo si cambia
        if (!existente.getPorcentaje().equals(datosNuevos.getPorcentaje())) {
            validarGrupoYPorcentaje(grupoId, id, datosNuevos.getPorcentaje());
        }

        existente.setTitulo(datosNuevos.getTitulo());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setTipo(datosNuevos.getTipo());
        existente.setPorcentaje(datosNuevos.getPorcentaje());
        existente.setFechaEntrega(datosNuevos.getFechaEntrega());

        return evaluacionRepository.save(existente);
    }

    @Transactional
    public void eliminarEvaluacion(Long id) {
        obtenerPorId(id);
        evaluacionRepository.deleteById(id);
    }

    // ─── Validación de porcentaje ─────────────────────────────────────────────

    /**
     * Valida que la suma de porcentajes del grupo no supere 100%.
     * Si excludeId != null, se excluye esa evaluación (caso de edición).
     */
    private void validarGrupoYPorcentaje(Long grupoId, Long excludeId, Double nuevoPorcentaje) {
        if (grupoId == null) {
            throw new BusinessException("El ID del grupo es obligatorio.");
        }
        if (!grupoRepository.existsById(grupoId)) {
            throw new ResourceNotFoundException("Grupo no encontrado con ID: " + grupoId);
        }
        if (nuevoPorcentaje == null || nuevoPorcentaje <= 0) {
            throw new BusinessException("El porcentaje debe ser mayor a 0.");
        }

        Double sumaActual = evaluacionRepository.sumPorcentajeByGrupoId(grupoId, excludeId);
        if (sumaActual + nuevoPorcentaje > 100.0) {
            throw new BusinessException(String.format(
                "La suma de porcentajes del grupo supera el 100%%. " +
                "Suma actual: %.1f%%, intentando añadir: %.1f%%.",
                sumaActual, nuevoPorcentaje
            ));
        }
    }
}
