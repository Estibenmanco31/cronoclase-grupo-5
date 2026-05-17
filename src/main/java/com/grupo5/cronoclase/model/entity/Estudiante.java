package com.grupo5.cronoclase.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Patrón Builder para crear objetos fácilmente
@Entity
@Table(name = "estudiantes")

public class Estudiante extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String documentoID;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    @JsonManagedReference //Esto se puso para evitar el retorno infinito en el GET
    private List<Matricula> matriculas;

    


    // Quitamos @JsonIgnore y ponemos ManagedReference con su propio nombre
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "estudiante-entrega") 
    private List<Entrega> entregas;

}
