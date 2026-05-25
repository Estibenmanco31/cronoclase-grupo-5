package com.grupo5.cronoclase;

import com.grupo5.cronoclase.model.entity.*;
import com.grupo5.cronoclase.model.enums.DiaSemana;
import com.grupo5.cronoclase.repository.GrupoRepository;
import com.grupo5.cronoclase.repository.ProfesorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CronoclaseGrupo5Application {

	public static void main(String[] args) {
		SpringApplication.run(CronoclaseGrupo5Application.class, args);
	}

	@Bean
	public CommandLineRunner seedDatabase(ProfesorRepository profesorRepository, GrupoRepository grupoRepository) {
		return args -> {
			if (profesorRepository.count() == 0) {
				System.out.println("🌱 Sembrando base de datos con profesor y grupos por defecto...");

				// 1. Crear Profesor Luis Perez
				Profesor profesor = Profesor.builder()
						.nombre("Profesor Luis Perez")
						.email("luisperez@cronoclase.com")
						.documentoID("101010101")
						.password("password123")
						.activo(true)
						.contacto(ContactoEstudiante.builder()
								.telefono("555-123-456")
								.direccion("Calle 10 # 5-20")
								.build())
						.build();

				PerfilProfesor perfil = PerfilProfesor.builder()
						.biografia("Docente titular con amplia experiencia en la enseñanza de ingeniería y desarrollo web.")
						.especialidad("Bases de Datos e Ingeniería de Software")
						.oficina("Oficina 402 - Edificio Principal")
						.profesor(profesor)
						.build();

				profesor.setPerfil(perfil);
				profesorRepository.save(profesor);

				// 2. Crear grupos predeterminados asignados al profesor Luis Perez
				Grupo g1 = Grupo.builder()
						.nombre("Bases de Datos - Grupo A")
						.dia(DiaSemana.LUNES)
						.profesor(profesor)
						.build();

				Grupo g2 = Grupo.builder()
						.nombre("Desarrollo Web - Grupo B")
						.dia(DiaSemana.MIERCOLES)
						.profesor(profesor)
						.build();

				Grupo g3 = Grupo.builder()
						.nombre("Matemáticas Avanzadas - Grupo C")
						.dia(DiaSemana.VIERNES)
						.profesor(profesor)
						.build();

				grupoRepository.save(g1);
				grupoRepository.save(g2);
				grupoRepository.save(g3);

				// 3. Crear Profesora Maria Torres
				Profesor profMaria = Profesor.builder()
						.nombre("Profesora Maria Torres")
						.email("mariatorres@cronoclase.com")
						.documentoID("202020202")
						.password("password123")
						.activo(true)
						.contacto(ContactoEstudiante.builder()
								.telefono("555-987-654")
								.direccion("Avenida de las Ciencias #42")
								.build())
						.build();

				PerfilProfesor perfilMaria = PerfilProfesor.builder()
						.biografia("Especialista en ciencias exactas y métodos cuantitativos aplicados a la ingeniería.")
						.especialidad("Matemáticas y Métodos Numéricos")
						.oficina("Oficina 205 - Edificio de Ciencias")
						.profesor(profMaria)
						.build();

				profMaria.setPerfil(perfilMaria);
				profesorRepository.save(profMaria);

				// 4. Crear grupo asignado a Maria Torres (Día diferente: JUEVES)
				Grupo g4 = Grupo.builder()
						.nombre("Matemáticas Avanzadas - Grupo D")
						.dia(DiaSemana.JUEVES)
						.profesor(profMaria)
						.build();

				grupoRepository.save(g4);

				System.out.println("✅ Base de datos sembrada con éxito.");
			}
		};
	}
}
