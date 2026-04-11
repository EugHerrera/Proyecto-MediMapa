package cl.duoc.geocalizacion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;
import cl.duoc.geocalizacion.model.SucursalFarmacia;


public interface SucursalFarmaciaRepository extends JpaRepository<SucursalFarmacia, Long>{
@Query(value = "SELECT * FROM sucursal_farmacia " +
                   "WHERE activo = true " +
                   "AND latitud BETWEEN :minLat AND :maxLat " +
                   "AND longitud BETWEEN :minLon AND :maxLon", 
           nativeQuery = true)
    List<SucursalFarmacia> buscarEnRangoCoordenadas(
            @Param("minLat") double minLat, 
            @Param("maxLat") double maxLat, 
            @Param("minLon") double minLon, 
            @Param("maxLon") double maxLon);
}
