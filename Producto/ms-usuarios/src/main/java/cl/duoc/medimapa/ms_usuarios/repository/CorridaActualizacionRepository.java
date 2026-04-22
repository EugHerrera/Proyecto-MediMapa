package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.CorridaActualizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorridaActualizacionRepository extends JpaRepository<CorridaActualizacion, Long> {
}