package com.example.forms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from http://localhost:3001
        config.addAllowedOrigin("http://localhost:3000");

        // Allow all methods (GET, POST, PUT, DELETE, etc.)
        config.addAllowedMethod("*");

        // Allow all headers
        config.addAllowedHeader("*");

        // Expose additional headers to the client
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("Content-Disposition");
        config.addExposedHeader("Access-Control-Allow-Origin");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
