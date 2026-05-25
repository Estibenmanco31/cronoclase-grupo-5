package com.grupo5.cronoclase.repository;

import com.grupo5.cronoclase.model.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    // Buscar grupos por nombre (Derived Query)
    List<Grupo> findByNombreContainingIgnoreCase(String nombre);

    // Buscar todos los grupos de un profesor específico (Derived Query)
    List<Grupo> findByProfesorId(Long profesorId);

    // Verificar si un estudiante ya está inscrito en un grupo (@Query)
    @Query("SELECT COUNT(e) > 0 FROM Grupo g JOIN g.estudiantes e WHERE g.id = :grupoId AND e.id = :estudianteId")
    boolean existsEstudianteInGrupo(@Param("grupoId") Long grupoId, @Param("estudianteId") Long estudianteId);

    // Buscar grupos a los que pertenece un estudiante (@Query)
    @Query("SELECT g FROM Grupo g JOIN g.estudiantes e WHERE e.id = :estudianteId")
    List<Grupo> findByEstudianteId(@Param("estudianteId") Long estudianteId);
}
