package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.grupo5.cronoclase.model.enums.TipoEvaluacion;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "evaluaciones")
public class Evaluacion extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvaluacion tipo;

    @Column(nullable = false)
    private Double porcentaje;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    // Grupo al que pertenece la evaluación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    @JsonBackReference(value = "grupo-evaluacion")
    private Grupo grupo;

    // Entregas de los estudiantes para esta evaluación
    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "evaluacion-entrega")
    @Builder.Default
    private List<Entrega> entregas = new ArrayList<>();
}
