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
@Table(name = "estudiantes")
public class Estudiante extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String documentoID;

    @Column(nullable = false, length = 100)
    private String password;

    // Datos de contacto embebidos (@Embeddable)
    @Embedded
    private ContactoEstudiante contacto;

    // Grupos en los que está inscrito el estudiante (lado inverso del @ManyToMany)
    @ManyToMany(mappedBy = "estudiantes", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Grupo> grupos = new ArrayList<>();

    // Entregas realizadas por el estudiante (@OneToMany)
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "estudiante-entrega")
    @Builder.Default
    private List<Entrega> entregas = new ArrayList<>();
}
