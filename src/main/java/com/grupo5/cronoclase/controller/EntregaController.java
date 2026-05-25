package com.grupo5.cronoclase.controller;

import com.grupo5.cronoclase.dtos.CalificarRequestDTO;
import com.grupo5.cronoclase.dtos.EntregaRequestDTO;
import com.grupo5.cronoclase.dtos.EntregaResponseDTO;
import com.grupo5.cronoclase.exception.BusinessException;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.service.EntregaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Entregas", description = "Gestión de entregas y calificaciones")
@RestController
@RequestMapping("/api/entrega")
@RequiredArgsConstructor
public class EntregaController {

    private final EntregaService entregaService;

    // ─── ESTUDIANTE: Crear / Actualizar entrega ───────────────────────────────

    @Operation(summary = "Enviar entrega (si ya existe para esta evaluación, la sobrescribe)")
    @PostMapping
    public ResponseEntity<EntregaResponseDTO> enviarEntrega(@Valid @RequestBody EntregaRequestDTO dto) {
        Entrega entrega = new Entrega();
        entrega.setFechaEntregaReal(dto.getFechaEntregaReal());
        entrega.setArchivoUrl(dto.getArchivoUrl());
        entrega.setComentario(dto.getComentario());

        Estudiante est = new Estudiante();
        est.setId(dto.getEstudianteId());
        entrega.setEstudiante(est);

        Evaluacion eval = new Evaluacion();
        eval.setId(dto.getEvaluacionId());
        entrega.setEvaluacion(eval);

        Entrega creada = entregaService.crearOActualizarEntrega(entrega);
        return new ResponseEntity<>(EntregaResponseDTO.fromEntity(creada), HttpStatus.CREATED);
    }

    // ─── LISTADOS ────────────────────────────────────────────────────────────

    @Operation(summary = "Listar todas las entregas")
    @GetMapping
    public ResponseEntity<List<EntregaResponseDTO>> obtenerTodas() {
        List<EntregaResponseDTO> dtos = entregaService.obtenerTodas().stream()
                .map(EntregaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una entrega por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntregaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Entrega entrega = entregaService.obtenerPorId(id);
        return ResponseEntity.ok(EntregaResponseDTO.fromEntity(entrega));
    }

    @Operation(summary = "Ver mis entregas (panel del estudiante)")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<EntregaResponseDTO>> obtenerPorEstudiante(@PathVariable Long estudianteId) {
        List<EntregaResponseDTO> dtos = entregaService.obtenerPorEstudiante(estudianteId).stream()
                .map(EntregaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Ver entregas de una evaluación específica (panel del profesor para calificar)")
    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<List<EntregaResponseDTO>> obtenerPorEvaluacion(@PathVariable Long evaluacionId) {
        List<EntregaResponseDTO> dtos = entregaService.obtenerPorEvaluacion(evaluacionId).stream()
                .map(EntregaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Ver todas las entregas de un grupo (panel general del profesor)")
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<EntregaResponseDTO>> obtenerPorGrupo(@PathVariable Long grupoId) {
        List<EntregaResponseDTO> dtos = entregaService.obtenerPorGrupo(grupoId).stream()
                .map(EntregaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar entregas por nombre de estudiante")
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<EntregaResponseDTO>> buscarPorNombreEstudiante(@PathVariable String nombre) {
        List<EntregaResponseDTO> dtos = entregaService.buscarPorNombreEstudiante(nombre).stream()
                .map(EntregaResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ─── PROFESOR: Calificar ─────────────────────────────────────────────────

    @Operation(summary = "Calificar una entrega (admite la nota por query parameter ?nota=4.5 o mediante un body JSON)")
    @PatchMapping("/{id}/calificar")
    public ResponseEntity<EntregaResponseDTO> calificarEntrega(
            @PathVariable Long id,
            @RequestParam(required = false) Double nota,
            @RequestBody(required = false) CalificarRequestDTO dto) {

        Double notaFinal = nota;
        if (notaFinal == null && dto != null) {
            notaFinal = dto.getNota();
        }

        if (notaFinal == null) {
            throw new BusinessException("Debe proporcionar la nota por query parameter (?nota=...) o en el body JSON.");
        }

        Entrega entrega = entregaService.calificarEntrega(id, notaFinal);
        return ResponseEntity.ok(EntregaResponseDTO.fromEntity(entrega));
    }

    // ─── SISTEMA: Recalcular estados ─────────────────────────────────────────

    @Operation(summary = "Recalcular estados de entregas pendientes (útil para detectar vencimientos)")
    @PutMapping("/actualizar-estados")
    public ResponseEntity<Map<String, Object>> actualizarEstados() {
        int actualizadas = entregaService.actualizarEstadosPendientes();
        return ResponseEntity.ok(Map.of("entregas_actualizadas", actualizadas));
    }

    // ─── ELIMINAR ────────────────────────────────────────────────────────────

    @Operation(summary = "Eliminar una entrega")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEntrega(@PathVariable Long id) {
        entregaService.eliminarEntrega(id);
        return ResponseEntity.noContent().build();
    }
}
