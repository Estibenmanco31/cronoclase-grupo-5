package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.entity.Evaluacion;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionResponseDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String tipo;
    private Double porcentaje;
    private LocalDate fechaEntrega;

    // Grupo al que pertenece
    private Long grupoId;
    private String grupoNombre;

    public static EvaluacionResponseDTO fromEntity(Evaluacion ev) {
        if (ev == null) {
            return null;
        }
        return EvaluacionResponseDTO.builder()
                .id(ev.getId())
                .titulo(ev.getTitulo())
                .descripcion(ev.getDescripcion())
                .tipo(ev.getTipo() != null ? ev.getTipo().name() : null)
                .porcentaje(ev.getPorcentaje())
                .fechaEntrega(ev.getFechaEntrega())
                .grupoId(ev.getGrupo() != null ? ev.getGrupo().getId() : null)
                .grupoNombre(ev.getGrupo() != null ? ev.getGrupo().getNombre() : null)
                .build();
    }
}
