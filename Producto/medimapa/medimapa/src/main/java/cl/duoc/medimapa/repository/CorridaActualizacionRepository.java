package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.CorridaActualizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorridaActualizacionRepository extends JpaRepository<CorridaActualizacion, Long> {
}