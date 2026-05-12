package cl.duoc.medimapa.ms_usuarios.config; 

import cl.duoc.medimapa.ms_usuarios.security.JwtFilter; 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para APIs REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                
                // Dejar pasar consultas "OPTIONS" invisibles del navegador
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 🔓 RUTAS PÚBLICAS: Swagger y Documentación
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // 🔥 OPCIÓN NUCLEAR: Metemos todo lo que te está dando problemas a la lista de acceso libre
                .requestMatchers(
                    "/api/usuarios/login", 
                    "/api/usuarios/registro", 
                    "/api/usuarios/solicitud-inscripcion",
                    "/api/usuarios/solicitudes/**", // Libera aprobar, rechazar y listar
                    "/api/usuarios/inventario/**",  // Libera todo lo del farmacéutico
                    "/api/usuarios/medicamentos-admin/**" // Libera el catálogo maestro
                ).permitAll()
                
                // 🔒 RESTO DE RUTAS
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}