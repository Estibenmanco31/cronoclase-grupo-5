package com.grupo5.cronoclase.dtos;

import com.grupo5.cronoclase.model.entity.Profesor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfesorResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private String documentoID;
    private Boolean activo;

    // Contacto (aplanado)
    private String telefono;
    private String direccion;

    // Perfil (aplanado)
    private String biografia;
    private String oficina;
    private String especialidad;

    public static ProfesorResponseDTO fromEntity(Profesor p) {
        if (p == null) {
            return null;
        }
        return ProfesorResponseDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .email(p.getEmail())
                .documentoID(p.getDocumentoID())
                .activo(p.getActivo())
                .telefono(p.getContacto() != null ? p.getContacto().getTelefono() : null)
                .direccion(p.getContacto() != null ? p.getContacto().getDireccion() : null)
                .biografia(p.getPerfil() != null ? p.getPerfil().getBiografia() : null)
                .oficina(p.getPerfil() != null ? p.getPerfil().getOficina() : null)
                .especialidad(p.getPerfil() != null ? p.getPerfil().getEspecialidad() : null)
                .build();
    }
}
