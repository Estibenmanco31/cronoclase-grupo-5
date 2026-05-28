package com.grupo5.cronoclase.repository;

import com.grupo5.cronoclase.model.entity.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    // Búsqueda flexible por nombre (Derived Query)
    List<Profesor> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda por email para login (Derived Query)
    Optional<Profesor> findByEmail(String email);

    Optional<Profesor> findByDocumentoID(String documentoID);

    boolean existsByEmail(String email);

    boolean existsByDocumentoID(String documentoID);
}
