package com.warplay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private GoogleAuthFilter googleAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(googleAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers("/api/clubs", "/api/clubs/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                
                // Public read-only crusade endpoints
                .requestMatchers("/api/crusades").permitAll()
                .requestMatchers("/api/crusades/{id}").permitAll()
                .requestMatchers("/api/crusades/club/{clubId}").permitAll()
                .requestMatchers("/api/crusades/club/{clubId}/active").permitAll()
                .requestMatchers("/api/crusades/search").permitAll()
                
                // Public read-only force endpoints (GET only)
                .requestMatchers("/api/forces/{id}").permitAll()
                .requestMatchers("/api/forces/club/{clubId}").permitAll()
                .requestMatchers("/api/forces/user/{userId}").permitAll()
                
                // Public read-only user-club endpoints
                .requestMatchers("/api/user-clubs/club/{clubId}").permitAll()
                .requestMatchers("/api/user-clubs/user/{userId}").permitAll()
                
                // Public read-only user endpoints
                .requestMatchers("/api/users/{id}").permitAll()
                .requestMatchers("/api/users/email/{email}").permitAll()
                
                // Authentication endpoints - no authentication required
                .requestMatchers("/api/auth/**").permitAll()
                
                // Public read-only MFM endpoints - no authentication required
                .requestMatchers("/api/mfm/**").permitAll()
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // Allow access to static resources and error pages
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (cannot use "*" with allowCredentials=true)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8080",
            "https://warplay.org",
            "https://www.warplay.org"
        ));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow credentials (important for authentication)
        configuration.setAllowCredentials(true);
        
        // Expose all headers that might be needed
        configuration.setExposedHeaders(Arrays.asList("*"));
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
