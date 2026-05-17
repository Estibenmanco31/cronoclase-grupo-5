package com.grupo5.cronoclase.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo5.cronoclase.repository.*;
import com.grupo5.cronoclase.dtos.*;
import com.grupo5.cronoclase.model.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service

public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    // Servicio para crear un solo estudiante

    public Estudiante crearEstudiante(Estudiante estudiante) {
        estudiante.setId(null); // Ignoramos cualquier id del body para forzar INSERT
        return estudianteRepository.save(estudiante);
    }


     


    // Servicio para crear varios estudiantes de una sola vez

    public List<Estudiante> crearVariosEstudiantes(List<Estudiante> estudiantes) {
        // Usamos el método que ya existe en el repositorio por herencia
        return estudianteRepository.saveAll(estudiantes);
    }

    // servicio para obtener todos los estudiantes

    public List<Estudiante> obtenerEstudiantes() {
        return estudianteRepository.findAll();
    }

    // Servicio para encotrar un estudiante por su documento de identidad

    public Estudiante findEstudianteByDocumento(String documentoID) {

        // El optional puede o no tene run estudiante. lo que se devuelve es el
        // estudiante que este dentro
        // del optional, de lo contrario se tira la excepcion.
        // como el optional es un contenedor, por eso necesita los metodos, el .get() y
        // el .isPresent

        Optional<Estudiante> estudianteEncontrado = estudianteRepository.findByDocumentoID(documentoID);

        if (estudianteEncontrado.isPresent()) {

            return estudianteEncontrado.get();
        }

        else {
            throw new RuntimeException("Estudiante no encontrado, verifique ID ingresado");
        }

    }

    public List<Estudiante> findEstudianteByNombre(String nombreEstudiante) {

        return estudianteRepository.findByNombreContainingIgnoreCase(nombreEstudiante);

    }

    public EstudianteResponseDTO obtenerPorId(Long id) {

        // 1. Buscamos la entidad completa en la BD
        Estudiante estudianteEntidad = estudianteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));
        EstudianteResponseDTO estudianteDTO = new EstudianteResponseDTO();
        estudianteDTO.setNombre(estudianteEntidad.getNombre());
        estudianteDTO.setEmail(estudianteEntidad.getEmail());

        return estudianteDTO;

    }


    private Estudiante buscarEntidadPorId(Long id) {
        return estudianteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con ID: " + id));
    }


    // --- ACTUALIZAR ESTUDIANTE ---
    @Transactional
    public Estudiante actualizarEstudiante(Long id, Estudiante datosNuevos) {
        // 1. Buscamos al estudiante actual (si no existe, lanza el error que ya
        // programaste)
        Estudiante estudianteExistente = buscarEntidadPorId(id);

        // 2. Seteamos los nuevos datos
        estudianteExistente.setNombre(datosNuevos.getNombre());
        estudianteExistente.setEmail(datosNuevos.getEmail());
        estudianteExistente.setDocumentoID(datosNuevos.getDocumentoID());

        // 3. Guardamos (JPA hace el UPDATE automáticamente)
        return estudianteRepository.save(estudianteExistente);
    }



    // --- ELIMINAR ESTUDIANTE ---
    @Transactional
    public void eliminarEstudiante(Long id) {
        // Validamos que existe antes de intentar borrar
        obtenerPorId(id);
        
        // Al ejecutar esto, por el CascadeType.ALL que tienes en la Entidad,
        // se borrarán también sus MATRICULAS y sus ENTREGAS.
        estudianteRepository.deleteById(id);
    }

}
