package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.grupo5.cronoclase.model.enums.*;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Patrón Builder para crear objetos fácilmente
@Entity
@Table(name = "matriculas")

public class Matricula extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estadoMatricula;

    // Sincronizamos el value con el ManagedReference de Estudiante
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    @JsonBackReference(value = "estudiante-matricula")
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
     private Grupo grupo;

}
