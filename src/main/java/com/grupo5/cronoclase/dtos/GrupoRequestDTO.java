package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.enums.DiaSemana;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GrupoRequestDTO {

    @NotBlank(message = "El nombre del grupo es obligatorio")
    private String nombre;

    @NotNull(message = "El día del grupo es obligatorio")
    private DiaSemana dia;

    @NotNull(message = "El profesor es obligatorio")
    private ProfesorIdDTO profesor;

    @Data
    public static class ProfesorIdDTO {
        @NotNull(message = "El ID del profesor es obligatorio")
        private Long id;
    }
}
