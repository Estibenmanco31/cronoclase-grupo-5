package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.grupo5.cronoclase.model.enums.DiaSemana;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grupos")
public class Grupo extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana dia;

    // Profesor responsable del grupo (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    @JsonIgnoreProperties({"grupos", "perfil", "hibernateLazyInitializer", "handler"})
    private Profesor profesor;

    // Inscripción directa de estudiantes al grupo (@ManyToMany)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "grupo_estudiante",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    @JsonIgnoreProperties({"grupos", "entregas", "contacto", "hibernateLazyInitializer", "handler"})
    @Builder.Default
    private List<Estudiante> estudiantes = new ArrayList<>();

    // Evaluaciones asignadas al grupo (@OneToMany)
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "grupo-evaluacion")
    @Builder.Default
    private List<Evaluacion> evaluaciones = new ArrayList<>();
}
