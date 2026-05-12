package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.SucursalFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SucursalFarmaciaRepository extends JpaRepository<SucursalFarmacia, Long> {
    
    List<SucursalFarmacia> findByFarmacia_Id(Long idFarmacia);
}