package com.jh.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class Config {
    @Bean
    CorsWebFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();
        config.applyPermitDefaultValues();
        // Possibly...
        // config.applyPermitDefaultValues()

//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("https://domain1.com");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
