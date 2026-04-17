package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Magia pura de Spring Boot: Con solo nombrar este método, él arma el SQL por debajo.
    Optional<Usuario> findByCorreo(String correo);
}