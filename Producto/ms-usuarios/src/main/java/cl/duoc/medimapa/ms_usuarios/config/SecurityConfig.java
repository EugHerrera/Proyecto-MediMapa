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
            // 1. 🔥 ACTIVAMOS EL CORS A NIVEL DE SEGURIDAD
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // APAGAMOS LA PROTECCIÓN CSRF PARA PERMITIR POST/PUT/DELETE
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 2. 🔥 PERMITIMOS LAS PETICIONES PREFLIGHT DEL NAVEGADOR
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                
                // Rutas públicas de búsqueda y scraping
                .requestMatchers("/api/buscador/**", "/api/scraper/**").permitAll()

                // Reglas VIP de Usuarios (Login, Registro y Solicitud)
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registro", "/api/usuarios/solicitud-inscripcion").permitAll()
                
                // 🔥 LA SOLUCIÓN NUCLEAR: Permiso absoluto a TODAS las rutas de inventario
                .requestMatchers("/api/usuarios/inventario/**").permitAll()
                
                // Todo lo demás requiere token
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3. 🔥 CONFIGURACIÓN EXACTA DE QUIÉN PUEDE ENTRAR Y CON QUÉ
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}