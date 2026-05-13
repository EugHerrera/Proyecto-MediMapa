package cl.duoc.geocalizacion.repository;

import cl.duoc.geocalizacion.model.SucursalFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalFarmaciaRepository extends JpaRepository<SucursalFarmacia, Long> {

    @Query(value = "SELECT * FROM sucursal_farmacia s WHERE ST_DWithin(CAST(s.ubicacion AS geography), CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography), :distancia)", nativeQuery = true)
    List<SucursalFarmacia> buscarCercanas(@Param("lat") double lat, @Param("lng") double lng, @Param("distancia") double distancia);
}