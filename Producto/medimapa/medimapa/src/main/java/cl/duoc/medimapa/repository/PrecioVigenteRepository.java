package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.model.PrecioVigenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, PrecioVigenteId> {
    // Aquí Spring hace toda la magia de los INSERT y UPDATE automáticamente
}