package com.grupo5.cronoclase.service;

import com.grupo5.cronoclase.exception.BusinessException;
import com.grupo5.cronoclase.exception.ResourceNotFoundException;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.model.enums.EstadoEntrega;
import com.grupo5.cronoclase.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EstudianteRepository estudianteRepository;

    // ─── ESTADO ──────────────────────────────────────────────────────────────

    /**
     * Calcula el estado de la entrega según las fechas.
     * · CALIFICADO  → no cambia automáticamente
     * · Sin fecha real + plazo vigente  → PENDIENTE
     * · Sin fecha real + plazo vencido  → TARDE
     * · Fecha real ≤ fechaLímite        → ENTREGADO
     * · Fecha real > fechaLímite        → TARDE
     */
    public EstadoEntrega calcularEstado(LocalDate fechaLimite, LocalDate fechaReal, EstadoEntrega estadoActual) {
        if (estadoActual == EstadoEntrega.CALIFICADO) return EstadoEntrega.CALIFICADO;

        if (fechaReal == null) {
            return LocalDate.now().isAfter(fechaLimite) ? EstadoEntrega.TARDE : EstadoEntrega.PENDIENTE;
        }
        return fechaReal.isAfter(fechaLimite) ? EstadoEntrega.TARDE : EstadoEntrega.ENTREGADO;
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    /**
     * Crea o sobrescribe una entrega.
     * Regla: Solo existe una entrega por estudiante por evaluación.
     * Si ya existe → actualiza fechaEntregaReal, archivoUrl y comentario.
     */
    @Transactional
    public Entrega crearOActualizarEntrega(Entrega entrega) {
        Long estudianteId = entrega.getEstudiante().getId();
        Long evaluacionId = entrega.getEvaluacion().getId();

        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + evaluacionId));

        if (!estudianteRepository.existsById(estudianteId)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId);
        }

        Optional<Entrega> existenteOpt = entregaRepository.findByEstudianteIdAndEvaluacionId(estudianteId, evaluacionId);

        LocalDate fechaReal = entrega.getFechaEntregaReal() != null ? entrega.getFechaEntregaReal() : LocalDate.now();
        EstadoEntrega nuevoEstado = calcularEstado(evaluacion.getFechaEntrega(), fechaReal, EstadoEntrega.PENDIENTE);

        if (existenteOpt.isPresent()) {
            // ── Sobrescribir entrega existente ──
            Entrega existente = existenteOpt.get();
            existente.setFechaEntregaReal(fechaReal);
            existente.setArchivoUrl(entrega.getArchivoUrl());
            existente.setComentario(entrega.getComentario());
            if (existente.getEstado() != EstadoEntrega.CALIFICADO) {
                existente.setEstado(nuevoEstado);
            }
            return entregaRepository.save(existente);
        }

        // ── Crear nueva entrega ──
        entrega.setId(null);
        entrega.setFechaEntregaReal(fechaReal);
        entrega.setEstado(nuevoEstado);
        entrega.setEvaluacion(evaluacion);
        return entregaRepository.save(entrega);
    }

    public List<Entrega> obtenerTodas() {
        return entregaRepository.findAll();
    }

    public Entrega obtenerPorId(Long id) {
        return entregaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));
    }

    public List<Entrega> obtenerPorEstudiante(Long estudianteId) {
        return entregaRepository.findByEstudianteId(estudianteId);
    }

    public List<Entrega> obtenerPorEvaluacion(Long evaluacionId) {
        return entregaRepository.findByEvaluacionId(evaluacionId);
    }

    public List<Entrega> obtenerPorGrupo(Long grupoId) {
        return entregaRepository.findByGrupoId(grupoId);
    }

    public List<Entrega> buscarPorNombreEstudiante(String nombre) {
        return entregaRepository.findByEstudianteNombre(nombre);
    }

    /**
     * Permite al PROFESOR calificar una entrega con una nota de 0.0 a 5.0.
     * El estado pasa a CALIFICADO y ya no cambia automáticamente.
     */
    @Transactional
    public Entrega calificarEntrega(Long id, Double nota) {
        if (nota == null || nota < 0.0 || nota > 5.0) {
            throw new BusinessException("La nota debe estar entre 0.0 y 5.0");
        }
        Entrega entrega = obtenerPorId(id);
        entrega.setNota(nota);
        entrega.setEstado(EstadoEntrega.CALIFICADO);
        return entregaRepository.save(entrega);
    }

    /**
     * Recalcula el estado de todas las entregas PENDIENTES.
     * Útil para ejecutar periódicamente y detectar entregas vencidas.
     */
    @Transactional
    public int actualizarEstadosPendientes() {
        List<Entrega> todas = entregaRepository.findAll();
        int actualizadas = 0;
        for (Entrega entrega : todas) {
            if (entrega.getEstado() == EstadoEntrega.PENDIENTE) {
                EstadoEntrega nuevo = calcularEstado(
                        entrega.getEvaluacion().getFechaEntrega(),
                        entrega.getFechaEntregaReal(),
                        entrega.getEstado()
                );
                if (nuevo != entrega.getEstado()) {
                    entrega.setEstado(nuevo);
                    entregaRepository.save(entrega);
                    actualizadas++;
                }
            }
        }
        return actualizadas;
    }

    @Transactional
    public void eliminarEntrega(Long id) {
        obtenerPorId(id);
        entregaRepository.deleteById(id);
    }
}
