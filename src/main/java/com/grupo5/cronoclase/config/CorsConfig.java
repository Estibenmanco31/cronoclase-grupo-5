package com.grupo5.cronoclase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Habilita CORS para todos los endpoints del backend
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Permite cualquier origen de forma segura (incluyendo localhost de Vite)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Permite el intercambio de credenciales si en el futuro se usan cookies/sesiones
    }
}
