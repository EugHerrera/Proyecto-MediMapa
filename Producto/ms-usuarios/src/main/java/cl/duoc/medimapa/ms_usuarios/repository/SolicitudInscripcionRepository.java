package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.SolicitudInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudInscripcionRepository extends JpaRepository<SolicitudInscripcion, Long> {
    
    @Query(value = "SELECT * FROM solicitud_inscripcion WHERE estado_solicitud = :estado", nativeQuery = true)
    List<SolicitudInscripcion> buscarPorEstado(@Param("estado") String estado);
}