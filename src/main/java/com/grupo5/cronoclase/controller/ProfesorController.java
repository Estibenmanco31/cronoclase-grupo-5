package com.grupo5.cronoclase.controller;

import com.grupo5.cronoclase.dtos.LoginRequestDTO;
import com.grupo5.cronoclase.dtos.ProfesorRequestDTO;
import com.grupo5.cronoclase.dtos.ProfesorResponseDTO;
import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.service.ProfesorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Profesores", description = "Gestión de profesores y autenticación")
@RestController
@RequestMapping("/api/profesor")
@RequiredArgsConstructor
public class ProfesorController {

    private final ProfesorService profesorService;

    @Operation(summary = "Registrar un nuevo profesor")
    @PostMapping
    public ResponseEntity<ProfesorResponseDTO> crearProfesor(@Valid @RequestBody ProfesorRequestDTO dto) {
        Profesor profesor = new Profesor();
        profesor.setNombre(dto.getNombre());
        profesor.setEmail(dto.getEmail());
        profesor.setDocumentoID(dto.getDocumentoID());
        profesor.setPassword(dto.getPassword());
        profesor.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

        if (dto.getTelefono() != null || dto.getDireccion() != null) {
            profesor.setContacto(ContactoEstudiante.builder()
                    .telefono(dto.getTelefono())
                    .direccion(dto.getDireccion())
                    .build());
        }
        if (dto.getBiografia() != null || dto.getOficina() != null || dto.getEspecialidad() != null) {
            profesor.setPerfil(PerfilProfesor.builder()
                    .biografia(dto.getBiografia())
                    .oficina(dto.getOficina())
                    .especialidad(dto.getEspecialidad())
                    .build());
        }
        Profesor creado = profesorService.crearProfesor(profesor);
        return new ResponseEntity<>(ProfesorResponseDTO.fromEntity(creado), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los profesores")
    @GetMapping
    public ResponseEntity<List<ProfesorResponseDTO>> obtenerTodos() {
        List<ProfesorResponseDTO> dtos = profesorService.obtenerTodos().stream()
                .map(ProfesorResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un profesor por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> obtenerPorId(@PathVariable Long id) {
        Profesor profesor = profesorService.obtenerPorId(id);
        return ResponseEntity.ok(ProfesorResponseDTO.fromEntity(profesor));
    }

    @Operation(summary = "Buscar profesores por nombre")
    @GetMapping("/buscar/{nombre}")
    public ResponseEntity<List<ProfesorResponseDTO>> buscarPorNombre(@PathVariable String nombre) {
        List<ProfesorResponseDTO> dtos = profesorService.buscarPorNombre(nombre).stream()
                .map(ProfesorResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Actualizar datos de un profesor")
    @PutMapping("/{id}")
    public ResponseEntity<ProfesorResponseDTO> actualizarProfesor(@PathVariable Long id, @Valid @RequestBody ProfesorRequestDTO dto) {
        Profesor datos = new Profesor();
        datos.setNombre(dto.getNombre());
        datos.setEmail(dto.getEmail());
        datos.setDocumentoID(dto.getDocumentoID());
        datos.setPassword(dto.getPassword());
        datos.setActivo(dto.getActivo());
        if (dto.getTelefono() != null || dto.getDireccion() != null) {
            datos.setContacto(ContactoEstudiante.builder()
                    .telefono(dto.getTelefono())
                    .direccion(dto.getDireccion())
                    .build());
        }
        if (dto.getBiografia() != null || dto.getOficina() != null || dto.getEspecialidad() != null) {
            datos.setPerfil(PerfilProfesor.builder()
                    .biografia(dto.getBiografia())
                    .oficina(dto.getOficina())
                    .especialidad(dto.getEspecialidad())
                    .build());
        }
        Profesor actualizado = profesorService.actualizarProfesor(id, datos);
        return ResponseEntity.ok(ProfesorResponseDTO.fromEntity(actualizado));
    }

    @Operation(summary = "Eliminar un profesor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProfesor(@PathVariable Long id) {
        profesorService.eliminarProfesor(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Login del profesor con email y contraseña")
    @PostMapping("/login")
    public ResponseEntity<ProfesorResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        Profesor loggedIn = profesorService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(ProfesorResponseDTO.fromEntity(loggedIn));
    }
}
