package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.enums.TipoEvaluacion;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionRequestDTO {

    @NotBlank(message = "El título de la evaluación es obligatorio")
    private String titulo;

    private String descripcion;


    @NotNull(message = "El tipo de evaluación es obligatorio")
    private TipoEvaluacion tipo;

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(value = "0.1", message = "El porcentaje mínimo es 0.1%")
    @DecimalMax(value = "100.0", message = "El porcentaje máximo es 100.0%")
    private Double porcentaje;

    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDate fechaEntrega;

    @NotNull(message = "El ID del grupo es obligatorio")
    private Long grupoId;
}
