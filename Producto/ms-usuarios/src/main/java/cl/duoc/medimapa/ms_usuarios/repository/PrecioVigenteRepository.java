package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, Long> {
    
    @Query("SELECT p FROM PrecioVigente p WHERE LOWER(p.textoBusqueda) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<PrecioVigente> buscarPorTexto(@Param("texto") String texto);
    
    @Query(value = "SELECT * FROM precio_vigente WHERE id_sucursal = :idSucursal", nativeQuery = true)
    List<PrecioVigente> buscarPorSucursal(@Param("idSucursal") Long idSucursal);
}