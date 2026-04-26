package cl.duoc.medimapa.ms_usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cl.duoc.medimapa.ms_usuarios.security.JwtFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permitimos peticiones de control del navegador (Preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                
                // Rutas públicas de búsqueda
                .requestMatchers("/api/buscador/**", "/api/scraper/**").permitAll()

                // 🔥 RUTAS VIP: Añadimos "/error" para que no te mienta con un 403 cuando algo falle en Java
                .requestMatchers(
                    "/api/usuarios/login", 
                    "/api/usuarios/registro", 
                    "/api/usuarios/solicitud-inscripcion", 
                    "/api/usuarios/admin/subir-isp", 
                    "/error"
                ).permitAll()
                
                // Permitir rutas de inventario temporalmente para pruebas
                .requestMatchers("/api/usuarios/inventario/**").permitAll()
                
                // Cualquier otra ruta requiere el token JWT
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitimos el puerto de React (5173) y el del Gateway (8080)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}