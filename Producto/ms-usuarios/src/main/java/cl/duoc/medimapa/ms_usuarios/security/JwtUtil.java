package cl.duoc.medimapa.ms_usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Llave estática para que los tokens sobrevivan a los reinicios del servidor.
    private static final String SECRET_PASS = "MediMapa2026DuocUCProyectoFinalArquitecturaSoftware";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_PASS.getBytes());
    
    // Tiempo de validez del Token: 10 horas
    private static final long TIEMPO_EXPIRACION = 1000 * 60 * 60 * 10;

    // 1. FABRICAR EL TOKEN
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol) // Aquí guardamos si es ADMIN o FARMACEUTICO
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 2. EXTRAER EL CORREO DEL TOKEN
    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    // 3. EXTRAER EL ROL DEL TOKEN
    public String extraerRol(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("rol", String.class);
    }

    // 4. VERIFICAR SI EL TOKEN ES VÁLIDO Y NO HA EXPIRADO
    public boolean validarToken(String token, String emailUsuario) {
        final String email = extraerEmail(token);
        return (email.equals(emailUsuario) && !tokenExpirado(token));
    }

    private boolean tokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}