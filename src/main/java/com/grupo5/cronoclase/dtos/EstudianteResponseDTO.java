package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.entity.Estudiante;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String documentoID;

    // Contacto (aplanado)
    private String telefono;
    private String direccion;

    public static EstudianteResponseDTO fromEntity(Estudiante e) {
        if (e == null) {
            return null;
        }
        return EstudianteResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .email(e.getEmail())
                .documentoID(e.getDocumentoID())
                .telefono(e.getContacto() != null ? e.getContacto().getTelefono() : null)
                .direccion(e.getContacto() != null ? e.getContacto().getDireccion() : null)
                .build();
    }
}
