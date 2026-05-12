package cl.duoc.medimapa.ms_usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MsUsuariosApplication {
    public static void main(String[] args) {
        
        
        System.out.println(" EL HASH EXACTO PARA 123456 ES: " + new BCryptPasswordEncoder().encode("123456"));
        
        SpringApplication.run(MsUsuariosApplication.class, args);
    }
}