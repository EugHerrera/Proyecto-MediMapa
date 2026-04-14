package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.SucursalFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<SucursalFarmacia, Long> {
    
    // Spring Boot armará el SQL: SELECT * FROM sucursal_farmacia WHERE activo = true
    List<SucursalFarmacia> findByActivoTrue();
}