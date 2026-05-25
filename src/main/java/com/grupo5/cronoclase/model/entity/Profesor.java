package com.grupo5.cronoclase.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "profesores")
public class Profesor extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String documentoID;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    // Datos de contacto embebidos (@Embeddable)
    @Embedded
    private ContactoEstudiante contacto;

    // Perfil académico del profesor (@OneToOne)
    @OneToOne(mappedBy = "profesor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "profesor-perfil")
    private PerfilProfesor perfil;

    // Grupos a cargo del profesor
    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Grupo> grupos = new ArrayList<>();
}
