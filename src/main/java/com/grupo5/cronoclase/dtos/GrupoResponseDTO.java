package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.entity.Grupo;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoResponseDTO {

    private Long id;
    private String nombre;
    private String dia;

    // Profesor (desacoplado)
    private Long profesorId;
    private String profesorNombre;

    // Estudiantes inscritos (desacoplados)
    private List<EstudianteResponseDTO> estudiantes;

    public static GrupoResponseDTO fromEntity(Grupo g) {
        if (g == null) {
            return null;
        }
        return GrupoResponseDTO.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .dia(g.getDia() != null ? g.getDia().name() : null)
                .profesorId(g.getProfesor() != null ? g.getProfesor().getId() : null)
                .profesorNombre(g.getProfesor() != null ? g.getProfesor().getNombre() : null)
                .estudiantes(g.getEstudiantes() != null ? g.getEstudiantes().stream()
                        .map(EstudianteResponseDTO::fromEntity)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }
}
