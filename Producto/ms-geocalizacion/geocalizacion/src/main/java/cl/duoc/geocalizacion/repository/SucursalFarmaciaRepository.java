package cl.duoc.geocalizacion.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.duoc.geocalizacion.model.SucursalFarmacia;


public interface SucursalFarmaciaRepository extends JpaRepository<SucursalFarmacia, Long>{
    @Query(value = "SELECT * FROM sucursal_farmacia s " +
            "WHERE ST_DWithin(" +
            "  s.ubicacion::geography, " +
            "  ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, " +
            "  :radio" +
            ")", 
            nativeQuery = true)
        List<SucursalFarmacia> buscarCercanas(
            @Param("lat") double lat, 
            @Param("lon") double lon, 
            @Param("radio") double radio);
}
