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
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public Estudiante crearEstudiante(Estudiante estudiante) {
        if (estudianteRepository.existsByEmail(estudiante.getEmail())) {
            throw new BusinessException("El correo ya está en uso");
        }
        if (estudianteRepository.existsByDocumentoID(estudiante.getDocumentoID())) {
            throw new BusinessException("El número de documento ya está registrado");
        }
        estudiante.setId(null);
        return estudianteRepository.save(estudiante);
    }

    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAll();
    }

    public Estudiante obtenerPorId(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    public Estudiante buscarPorDocumento(String documentoID) {
        return estudianteRepository.findByDocumentoID(documentoID)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con documento: " + documentoID));
    }

    public List<Estudiante> buscarPorNombre(String nombre) {
        return estudianteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional
    public Estudiante actualizarEstudiante(Long id, Estudiante datosNuevos) {
        Estudiante existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        existente.setEmail(datosNuevos.getEmail());
        existente.setDocumentoID(datosNuevos.getDocumentoID());
        if (datosNuevos.getPassword() != null) {
            existente.setPassword(datosNuevos.getPassword());
        }
        if (datosNuevos.getContacto() != null) {
            existente.setContacto(datosNuevos.getContacto());
        }
        return estudianteRepository.save(existente);
    }

    @Transactional
    public void eliminarEstudiante(Long id) {
        obtenerPorId(id);
        estudianteRepository.deleteById(id);
    }

    // ─── AUTENTICACIÓN ───────────────────────────────────────────────────────

    public Estudiante login(String email, String password) {
        Estudiante estudiante = estudianteRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciales inválidas: Estudiante no encontrado"));
        if (!estudiante.getPassword().equals(password)) {
            throw new BusinessException("Credenciales inválidas: Contraseña incorrecta");
        }
        return estudiante;
    }
}
