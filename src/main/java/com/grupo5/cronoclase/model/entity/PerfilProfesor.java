package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "perfiles_profesor")
public class PerfilProfesor extends BaseEntity {

    @Column(length = 500)
    private String biografia;

    @Column(length = 50)
    private String oficina;

    @Column(length = 100)
    private String especialidad;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    @JsonBackReference(value = "profesor-perfil")
    private Profesor profesor;
}
