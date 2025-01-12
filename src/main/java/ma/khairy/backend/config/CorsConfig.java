package ma.khairy.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Autoriser tous les endpoints
                        .allowedOrigins("http://localhost:3000") // Autoriser le frontend React
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Méthodes HTTP autorisées
                        .allowedHeaders("*") // Autoriser tous les en-têtes
                        .allowCredentials(true); // Autoriser les cookies et les en-têtes d'authentification
            }
        };
    }
}