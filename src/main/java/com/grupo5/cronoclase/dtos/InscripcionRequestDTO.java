package com.grupo5.cronoclase.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InscripcionRequestDTO {

    @NotNull(message = "El ID del grupo es obligatorio")
    private Long grupoId;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long estudianteId;
}
