package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.grupo5.cronoclase.model.enums.*;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Patrón Builder para crear objetos fácilmente
@Entity
@Table(name = "evaluaciones")

public class Evaluacion extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvaluacion tipo;

    @Column(nullable = false)
    private Double porcentaje;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    // Corta el bucle hacia arriba con Grupo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    @JsonBackReference(value = "grupo-evaluacion")
    private Grupo grupo;

    // Abre la lista hacia las entregas usando el identificador único
    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "evaluacion-entrega")
    private List<Entrega> entregas;

}
