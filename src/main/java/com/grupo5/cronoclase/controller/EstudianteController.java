package com.grupo5.cronoclase.controller;

import com.grupo5.cronoclase.dtos.EstudianteRequestDTO;
import com.grupo5.cronoclase.dtos.EstudianteResponseDTO;
import com.grupo5.cronoclase.dtos.LoginRequestDTO;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.service.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Estudiantes", description = "Gestión de estudiantes y autenticación")
@RestController
@RequestMapping("/api/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Operation(summary = "Registrar un nuevo estudiante")
    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crearEstudiante(@Valid @RequestBody EstudianteRequestDTO dto) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(dto.getNombre());
        estudiante.setEmail(dto.getEmail());
        estudiante.setDocumentoID(dto.getDocumentoID());
        estudiante.setPassword(dto.getPassword());
        if (dto.getTelefono() != null || dto.getDireccion() != null) {
            estudiante.setContacto(ContactoEstudiante.builder()
                    .telefono(dto.getTelefono())
                    .direccion(dto.getDireccion())
                    .build());
        }
        Estudiante creado = estudianteService.crearEstudiante(estudiante);
        return new ResponseEntity<>(EstudianteResponseDTO.fromEntity(creado), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los estudiantes")
    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> obtenerTodos() {
        List<EstudianteResponseDTO> dtos = estudianteService.obtenerTodos().stream()
                .map(EstudianteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un estudiante por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> obtenerPorId(@PathVariable Long id) {
        Estudiante estudiante = estudianteService.obtenerPorId(id);
        return ResponseEntity.ok(EstudianteResponseDTO.fromEntity(estudiante));
    }

    @Operation(summary = "Buscar estudiante por documento de identidad")
    @GetMapping("/documento/{documentoID}")
    public ResponseEntity<EstudianteResponseDTO> buscarPorDocumento(@PathVariable String documentoID) {
        Estudiante estudiante = estudianteService.buscarPorDocumento(documentoID);
        return ResponseEntity.ok(EstudianteResponseDTO.fromEntity(estudiante));
    }

    @Operation(summary = "Buscar estudiantes por nombre")
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<EstudianteResponseDTO>> buscarPorNombre(@PathVariable String nombre) {
        List<EstudianteResponseDTO> dtos = estudianteService.buscarPorNombre(nombre).stream()
                .map(EstudianteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Actualizar datos de un estudiante")
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizarEstudiante(@PathVariable Long id, @Valid @RequestBody EstudianteRequestDTO dto) {
        Estudiante datos = new Estudiante();
        datos.setNombre(dto.getNombre());
        datos.setEmail(dto.getEmail());
        datos.setDocumentoID(dto.getDocumentoID());
        datos.setPassword(dto.getPassword());
        if (dto.getTelefono() != null || dto.getDireccion() != null) {
            datos.setContacto(ContactoEstudiante.builder()
                    .telefono(dto.getTelefono())
                    .direccion(dto.getDireccion())
                    .build());
        }
        Estudiante actualizado = estudianteService.actualizarEstudiante(id, datos);
        return ResponseEntity.ok(EstudianteResponseDTO.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar un estudiante")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEstudiante(@PathVariable Long id) {
        estudianteService.eliminarEstudiante(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Login del estudiante con email y contraseña")
    @PostMapping("/login")
    public ResponseEntity<EstudianteResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Estudiante loggedIn = estudianteService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(EstudianteResponseDTO.fromEntity(loggedIn));
    }
}
