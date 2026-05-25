package com.grupo5.cronoclase.controller;

import com.grupo5.cronoclase.dtos.GrupoRequestDTO;
import com.grupo5.cronoclase.dtos.GrupoResponseDTO;
import com.grupo5.cronoclase.dtos.InscripcionRequestDTO;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.service.GrupoService;
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

@Tag(name = "Grupos", description = "Gestión de grupos, inscripción de estudiantes y cálculo de notas")
@RestController
@RequestMapping("/api/grupo")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Operation(summary = "Crear un nuevo grupo")
    @PostMapping
    public ResponseEntity<GrupoResponseDTO> crearGrupo(@Valid @RequestBody GrupoRequestDTO dto) {
        Grupo grupo = new Grupo();
        grupo.setNombre(dto.getNombre());
        grupo.setDia(dto.getDia());

        Profesor prof = new Profesor();
        prof.setId(dto.getProfesor().getId());
        grupo.setProfesor(prof);

        Grupo creado = grupoService.crearGrupo(grupo);
        return new ResponseEntity<>(GrupoResponseDTO.fromEntity(creado), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los grupos")
    @GetMapping
    public ResponseEntity<List<GrupoResponseDTO>> obtenerTodos() {
        List<GrupoResponseDTO> dtos = grupoService.obtenerTodos().stream()
                .map(GrupoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un grupo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> obtenerPorId(@PathVariable Long id) {
        Grupo grupo = grupoService.obtenerPorId(id);
        return ResponseEntity.ok(GrupoResponseDTO.fromEntity(grupo));
    }

    @Operation(summary = "Buscar grupos por nombre")
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<GrupoResponseDTO>> buscarPorNombre(@PathVariable String nombre) {
        List<GrupoResponseDTO> dtos = grupoService.obtenerPorNombre(nombre).stream()
                .map(GrupoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ─── VISTAS POR ROL ──────────────────────────────────────────────────────

    @Operation(summary = "Ver grupos de un profesor (panel del profesor)")
    @GetMapping("/profesor/{profesorId}")
    public ResponseEntity<List<GrupoResponseDTO>> obtenerPorProfesor(@PathVariable Long profesorId) {
        List<GrupoResponseDTO> dtos = grupoService.obtenerPorProfesor(profesorId).stream()
                .map(GrupoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Ver grupos en los que está inscrito un estudiante (panel del estudiante)")
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<GrupoResponseDTO>> obtenerPorEstudiante(@PathVariable Long estudianteId) {
        List<GrupoResponseDTO> dtos = grupoService.obtenerPorEstudiante(estudianteId).stream()
                .map(GrupoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ─── INSCRIPCIÓN (PathVariables) ─────────────────────────────────────────

    @Operation(summary = "Inscribir un estudiante en un grupo (vía URL)")
    @PostMapping("/{grupoId}/inscribir/{estudianteId}")
    public ResponseEntity<GrupoResponseDTO> inscribirEstudiante(
            @PathVariable Long grupoId,
            @PathVariable Long estudianteId) {
        Grupo grupo = grupoService.inscribirEstudiante(grupoId, estudianteId);
        return new ResponseEntity<>(GrupoResponseDTO.fromEntity(grupo), HttpStatus.OK);
    }

    @Operation(summary = "Desinscribir un estudiante de un grupo (vía URL)")
    @DeleteMapping("/{grupoId}/desinscribir/{estudianteId}")
    public ResponseEntity<GrupoResponseDTO> desinscribirEstudiante(
            @PathVariable Long grupoId,
            @PathVariable Long estudianteId) {
        Grupo grupo = grupoService.desinscribirEstudiante(grupoId, estudianteId);
        return ResponseEntity.ok(GrupoResponseDTO.fromEntity(grupo));
    }

    // ─── INSCRIPCIÓN (JSON Body) ─────────────────────────────────────────────

    @Operation(summary = "Inscribir un estudiante en un grupo (vía body JSON)")
    @PostMapping("/inscribir")
    public ResponseEntity<GrupoResponseDTO> inscribirEstudianteJson(@Valid @RequestBody InscripcionRequestDTO dto) {
        Grupo grupo = grupoService.inscribirEstudiante(dto.getGrupoId(), dto.getEstudianteId());
        return new ResponseEntity<>(GrupoResponseDTO.fromEntity(grupo), HttpStatus.OK);
    }

    @Operation(summary = "Desinscribir un estudiante de un grupo (vía body JSON)")
    @PostMapping("/desinscribir")
    public ResponseEntity<GrupoResponseDTO> desinscribirEstudianteJson(@Valid @RequestBody InscripcionRequestDTO dto) {
        Grupo grupo = grupoService.desinscribirEstudiante(dto.getGrupoId(), dto.getEstudianteId());
        return ResponseEntity.ok(GrupoResponseDTO.fromEntity(grupo));
    }

    // ─── NOTA FINAL (PathVariable) ───────────────────────────────────────────

    @Operation(summary = "Calcular la nota final ponderada de un estudiante en un grupo (vía URL)")
    @GetMapping("/{grupoId}/estudiante/{estudianteId}/nota-final")
    public ResponseEntity<Map<String, Object>> calcularNotaFinal(
            @PathVariable Long grupoId,
            @PathVariable Long estudianteId) {
        Double nota = grupoService.calcularNotaFinal(grupoId, estudianteId);
        return ResponseEntity.ok(Map.of(
                "grupoId", grupoId,
                "estudianteId", estudianteId,
                "notaFinal", nota
        ));
    }

    // ─── NOTA FINAL (JSON Body) ──────────────────────────────────────────────

    @Operation(summary = "Calcular la nota final ponderada de un estudiante en un grupo (vía body JSON)")
    @PostMapping("/nota-final")
    public ResponseEntity<Map<String, Object>> calcularNotaFinalJson(@Valid @RequestBody InscripcionRequestDTO dto) {
        Double nota = grupoService.calcularNotaFinal(dto.getGrupoId(), dto.getEstudianteId());
        return ResponseEntity.ok(Map.of(
                "grupoId", dto.getGrupoId(),
                "estudianteId", dto.getEstudianteId(),
                "notaFinal", nota
        ));
    }

    // ─── ACTUALIZAR / ELIMINAR ───────────────────────────────────────────────

    @Operation(summary = "Actualizar datos de un grupo")
    @PutMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> actualizarGrupo(@PathVariable Long id, @Valid @RequestBody GrupoRequestDTO dto) {
        Grupo grupo = new Grupo();
        grupo.setNombre(dto.getNombre());
        grupo.setDia(dto.getDia());

        Profesor prof = new Profesor();
        prof.setId(dto.getProfesor().getId());
        grupo.setProfesor(prof);

        Grupo actualizado = grupoService.actualizarGrupo(id, grupo);
        return ResponseEntity.ok(GrupoResponseDTO.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar un grupo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGrupo(@PathVariable Long id) {
        grupoService.eliminarGrupo(id);
        return ResponseEntity.noContent().build();
    }
}
