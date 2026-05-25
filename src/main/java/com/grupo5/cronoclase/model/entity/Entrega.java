package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.grupo5.cronoclase.model.enums.EstadoEntrega;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "entregas")
public class Entrega extends BaseEntity {

    @Column(nullable = true)
    private LocalDate fechaEntregaReal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoEntrega estado = EstadoEntrega.PENDIENTE;

    // URL del archivo adjunto o enlace externo (Google Drive, GitHub, etc.)
    @Column(length = 500)
    private String archivoUrl;

    @Column(length = 1000)
    private String comentario;

    // Nota de 0.0 a 5.0 asignada por el profesor
    @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
    @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
    @Column(nullable = true)
    private Double nota;

    // Estudiante que realizó la entrega
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    @JsonBackReference(value = "estudiante-entrega")
    private Estudiante estudiante;

    // Evaluación a la que corresponde la entrega
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    @JsonBackReference(value = "evaluacion-entrega")
    private Evaluacion evaluacion;
}
