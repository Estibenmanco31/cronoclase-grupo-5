package com.grupo5.cronoclase.repository;

import com.grupo5.cronoclase.model.entity.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Long> {

    // Todas las entregas de un estudiante (Derived Query)
    List<Entrega> findByEstudianteId(Long estudianteId);

    // Todas las entregas de una evaluación específica para que el profesor pueda calificar (Derived Query)
    List<Entrega> findByEvaluacionId(Long evaluacionId);

    // Buscar entrega única de un estudiante por evaluación — clave para la regla de sobrescritura (Derived Query)
    Optional<Entrega> findByEstudianteIdAndEvaluacionId(Long estudianteId, Long evaluacionId);

    // Buscar entregas por nombre de estudiante, útil para el profesor (@Query)
    @Query("SELECT e FROM Entrega e WHERE LOWER(e.estudiante.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Entrega> findByEstudianteNombre(@Param("nombre") String nombre);

    // Todas las entregas de un grupo específico (para el panel del profesor) (@Query)
    @Query("SELECT e FROM Entrega e WHERE e.evaluacion.grupo.id = :grupoId")
    List<Entrega> findByGrupoId(@Param("grupoId") Long grupoId);
}
