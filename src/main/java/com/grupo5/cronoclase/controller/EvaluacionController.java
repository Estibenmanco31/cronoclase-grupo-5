package com.grupo5.cronoclase.controller;

import com.grupo5.cronoclase.dtos.EvaluacionRequestDTO;
import com.grupo5.cronoclase.dtos.EvaluacionResponseDTO;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.service.EvaluacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Evaluaciones", description = "Gestión de evaluaciones por grupo")
@RestController
@RequestMapping("/api/evaluacion")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @Operation(summary = "Crear una nueva evaluación para un grupo (el porcentaje total del grupo no puede superar 100%)")
    @PostMapping
    public ResponseEntity<EvaluacionResponseDTO> crearEvaluacion(@Valid @RequestBody EvaluacionRequestDTO dto) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setTitulo(dto.getTitulo());
        evaluacion.setDescripcion(dto.getDescripcion());
        evaluacion.setTipo(dto.getTipo());
        evaluacion.setPorcentaje(dto.getPorcentaje());
        evaluacion.setFechaEntrega(dto.getFechaEntrega());

        Grupo grupo = new Grupo();
        grupo.setId(dto.getGrupoId());
        evaluacion.setGrupo(grupo);

        Evaluacion creada = evaluacionService.crearEvaluacion(evaluacion);
        return new ResponseEntity<>(EvaluacionResponseDTO.fromEntity(creada), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las evaluaciones")
    @GetMapping
    public ResponseEntity<List<EvaluacionResponseDTO>> obtenerTodas() {
        List<EvaluacionResponseDTO> dtos = evaluacionService.obtenerTodas().stream()
                .map(EvaluacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una evaluación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionResponseDTO> obtenerPorId(@PathVariable Long id) {
        Evaluacion evaluacion = evaluacionService.obtenerPorId(id);
        return ResponseEntity.ok(EvaluacionResponseDTO.fromEntity(evaluacion));
    }

    @Operation(summary = "Ver evaluaciones de un grupo (acceso de estudiante y profesor)")
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<EvaluacionResponseDTO>> obtenerPorGrupo(@PathVariable Long grupoId) {
        List<EvaluacionResponseDTO> dtos = evaluacionService.obtenerPorGrupo(grupoId).stream()
                .map(EvaluacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Buscar evaluaciones por nombre de grupo")
    @GetMapping("/grupo/buscar/{nombre}")
    public ResponseEntity<List<EvaluacionResponseDTO>> buscarPorNombreGrupo(@PathVariable String nombre) {
        List<EvaluacionResponseDTO> dtos = evaluacionService.buscarPorNombreGrupo(nombre).stream()
                .map(EvaluacionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Actualizar una evaluación")
    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionResponseDTO> actualizarEvaluacion(@PathVariable Long id, @Valid @RequestBody EvaluacionRequestDTO dto) {
        Evaluacion datos = new Evaluacion();
        datos.setTitulo(dto.getTitulo());
        datos.setDescripcion(dto.getDescripcion());
        datos.setTipo(dto.getTipo());
        datos.setPorcentaje(dto.getPorcentaje());
        datos.setFechaEntrega(dto.getFechaEntrega());
        Evaluacion actualizada = evaluacionService.actualizarEvaluacion(id, datos);
        return ResponseEntity.ok(EvaluacionResponseDTO.fromEntity(actualizada));
    }

    @Operation(summary = "Eliminar una evaluación (elimina también sus entregas)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvaluacion(@PathVariable Long id) {
        evaluacionService.eliminarEvaluacion(id);
        return ResponseEntity.noContent().build();
    }
}
