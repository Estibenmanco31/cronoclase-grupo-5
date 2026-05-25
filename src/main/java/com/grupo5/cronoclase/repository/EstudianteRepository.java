package com.grupo5.cronoclase.repository;

import com.grupo5.cronoclase.model.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long>{

    Optional<Estudiante> findByDocumentoID(String documentoID);
    
    List<Estudiante> findByNombreContainingIgnoreCase(String nombreEstudiante);

    Optional<Estudiante> findByEmail(String email);
}
