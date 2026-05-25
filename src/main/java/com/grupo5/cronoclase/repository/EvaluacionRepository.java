package com.grupo5.cronoclase.repository;

import com.grupo5.cronoclase.model.entity.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    // Buscar evaluaciones de un grupo (Derived Query)
    List<Evaluacion> findByGrupoId(Long grupoId);

    // Buscar evaluaciones por nombre de grupo (Derived Query)
    List<Evaluacion> findByGrupoNombreContainingIgnoreCase(String nombre);

    // Sumar el porcentaje total de evaluaciones en un grupo, excluyendo opcionalmente una (@Query)
    @Query("SELECT COALESCE(SUM(e.porcentaje), 0) FROM Evaluacion e WHERE e.grupo.id = :grupoId AND (:excludeId IS NULL OR e.id <> :excludeId)")
    Double sumPorcentajeByGrupoId(@Param("grupoId") Long grupoId, @Param("excludeId") Long excludeId);
}
