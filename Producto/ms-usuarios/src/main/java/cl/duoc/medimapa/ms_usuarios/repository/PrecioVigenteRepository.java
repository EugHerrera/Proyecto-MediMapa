package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import cl.duoc.medimapa.ms_usuarios.model.PrecioVigenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, PrecioVigenteId> {
    
    // 🔥 MANTENIDO INTACTO: Tu buscador de palabras
    @Query("SELECT p FROM PrecioVigente p WHERE LOWER(p.id.texto_busqueda) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<PrecioVigente> buscarPorTexto(@Param("texto") String texto);
    
    // 🔥 ARREGLADO: Ahora es una consulta nativa a prueba de balas
    @Query(value = "SELECT * FROM precio_vigente WHERE id_sucursal = :idSucursal", nativeQuery = true)
    List<PrecioVigente> buscarPorSucursal(@Param("idSucursal") Long idSucursal);
}