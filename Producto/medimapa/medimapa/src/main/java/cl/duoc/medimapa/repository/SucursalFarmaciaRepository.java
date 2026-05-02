package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.SucursalFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SucursalFarmaciaRepository extends JpaRepository<SucursalFarmacia, Long> {
    
    // 🔥 MAGIA DE SPRING DATA: Busca todas las sucursales por el ID de la cadena (Marca)
    List<SucursalFarmacia> findByFarmacia_Id(Long idFarmacia);
}