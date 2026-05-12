package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.PrecioVigente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, Long> {
    
    @Query("SELECT p FROM PrecioVigente p WHERE LOWER(p.textoBusqueda) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<PrecioVigente> buscarPorNombreMedicamento(@Param("query") String query);
}