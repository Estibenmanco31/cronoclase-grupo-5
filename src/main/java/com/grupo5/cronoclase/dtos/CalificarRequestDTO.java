package com.grupo5.cronoclase.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CalificarRequestDTO {

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "0.0", message = "La nota mínima es 0.0")
    @DecimalMax(value = "5.0", message = "La nota máxima es 5.0")
    private Double nota;
}
