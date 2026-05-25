package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.entity.Entrega;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntregaResponseDTO {

    private Long id;
    private LocalDate fechaEntregaReal;
    private String estado;
    private String archivoUrl;
    private String comentario;
    private Double nota;

    // Estudiante (desacoplado)
    private Long estudianteId;
    private String estudianteNombre;

    // Evaluacion (desacoplado)
    private Long evaluacionId;
    private String evaluacionTitulo;

    public static EntregaResponseDTO fromEntity(Entrega en) {
        if (en == null) {
            return null;
        }
        return EntregaResponseDTO.builder()
                .id(en.getId())
                .fechaEntregaReal(en.getFechaEntregaReal())
                .estado(en.getEstado() != null ? en.getEstado().name() : null)
                .archivoUrl(en.getArchivoUrl())
                .comentario(en.getComentario())
                .nota(en.getNota())
                .estudianteId(en.getEstudiante() != null ? en.getEstudiante().getId() : null)
                .estudianteNombre(en.getEstudiante() != null ? en.getEstudiante().getNombre() : null)
                .evaluacionId(en.getEvaluacion() != null ? en.getEvaluacion().getId() : null)
                .evaluacionTitulo(en.getEvaluacion() != null ? en.getEvaluacion().getTitulo() : null)
                .build();
    }
}
