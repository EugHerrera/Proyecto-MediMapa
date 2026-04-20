package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.model.PrecioVigenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, PrecioVigenteId> {
    
    // Busca en la base de datos si el texto coincide, ignorando mayúsculas/minúsculas
    @Query("SELECT p FROM PrecioVigente p WHERE LOWER(p.id.texto_busqueda) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<PrecioVigente> buscarPorNombreMedicamento(@Param("query") String query);
}