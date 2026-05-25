package com.grupo5.cronoclase.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaRequestDTO {

    private LocalDate fechaEntregaReal;

    private String archivoUrl;

    private String comentario;

    @DecimalMin(value = "0.0", message = "La nota no puede ser menor a 0.0")
    @DecimalMax(value = "5.0", message = "La nota no puede ser mayor a 5.0")
    private Double nota;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El ID de la evaluación es obligatorio")
    private Long evaluacionId;
}
