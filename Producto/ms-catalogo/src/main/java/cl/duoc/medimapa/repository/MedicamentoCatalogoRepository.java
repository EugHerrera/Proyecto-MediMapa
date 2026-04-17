package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.MedicamentoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicamentoCatalogoRepository extends JpaRepository<MedicamentoCatalogo, Long> {
    
    // Encuentra el principio activo ignorando mayúsculas y minúsculas
    Optional<MedicamentoCatalogo> findByNombreComercialIgnoreCase(String nombreComercial);
}