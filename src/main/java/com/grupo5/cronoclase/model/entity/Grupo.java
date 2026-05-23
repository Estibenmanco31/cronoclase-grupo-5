package com.grupo5.cronoclase.model.entity;

import com.grupo5.cronoclase.model.enums.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

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
@Table(name = "grupos")

public class Grupo extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana dia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jornada jornada;

    // Cambiado de BackReference a IgnoreProperties para que el Front SÍ vea el curso
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties({ "grupos", "hibernateLazyInitializer", "handler" })
    private Curso curso;

    // Se queda con @JsonIgnore porque el viaje empezó desde el estudiante
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<Matricula> matriculas;

    // Cambiado a ManagedReference para que el Front pueda listar las evaluaciones del grupo
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "grupo-evaluacion")
    private List<Evaluacion> evaluaciones;

    // Perfecto, se queda así para ver los datos del profe sin bucles
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    @JsonIgnoreProperties({ "grupos", "hibernateLazyInitializer", "handler" }) 
    private Profesor profesor;

}
