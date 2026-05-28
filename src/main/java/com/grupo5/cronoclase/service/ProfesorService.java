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
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public Profesor crearProfesor(Profesor profesor) {
        if (profesorRepository.existsByEmail(profesor.getEmail())) {
            throw new BusinessException("El correo ya está en uso");
        }
        if (profesorRepository.existsByDocumentoID(profesor.getDocumentoID())) {
            throw new BusinessException("El número de documento ya está registrado");
        }
        profesor.setId(null);
        if (profesor.getActivo() == null) profesor.setActivo(true);
        // Asignar la referencia bidireccional al perfil antes de persistir
        if (profesor.getPerfil() != null) {
            profesor.getPerfil().setProfesor(profesor);
        }
        return profesorRepository.save(profesor);
    }

    public List<Profesor> obtenerTodos() {
        return profesorRepository.findAll();
    }

    public Profesor obtenerPorId(Long id) {
        return profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));
    }

    public List<Profesor> buscarPorNombre(String nombre) {
        List<Profesor> resultado = profesorRepository.findByNombreContainingIgnoreCase(nombre);
        if (resultado.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron profesores con nombre: " + nombre);
        }
        return resultado;
    }

    @Transactional
    public Profesor actualizarProfesor(Long id, Profesor datosNuevos) {
        Profesor existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        existente.setEmail(datosNuevos.getEmail());
        existente.setDocumentoID(datosNuevos.getDocumentoID());
        existente.setActivo(datosNuevos.getActivo());
        if (datosNuevos.getPassword() != null) {
            existente.setPassword(datosNuevos.getPassword());
        }
        if (datosNuevos.getContacto() != null) {
            existente.setContacto(datosNuevos.getContacto());
        }
        // Actualizar perfil si existe
        if (datosNuevos.getPerfil() != null) {
            if (existente.getPerfil() != null) {
                existente.getPerfil().setBiografia(datosNuevos.getPerfil().getBiografia());
                existente.getPerfil().setOficina(datosNuevos.getPerfil().getOficina());
                existente.getPerfil().setEspecialidad(datosNuevos.getPerfil().getEspecialidad());
            } else {
                datosNuevos.getPerfil().setProfesor(existente);
                existente.setPerfil(datosNuevos.getPerfil());
            }
        }
        return profesorRepository.save(existente);
    }

    @Transactional
    public void eliminarProfesor(Long id) {
        obtenerPorId(id);
        profesorRepository.deleteById(id);
    }

    // ─── AUTENTICACIÓN ───────────────────────────────────────────────────────

    public Profesor login(String email, String password) {
        Profesor profesor = profesorRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciales inválidas: Profesor no encontrado"));
        if (!profesor.getPassword().equals(password)) {
            throw new BusinessException("Credenciales inválidas: Contraseña incorrecta");
        }
        return profesor;
    }
}
