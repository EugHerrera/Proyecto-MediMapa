package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.PrecioVigente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, Long> {

    @Query("SELECT pv FROM PrecioVigente pv " +
           "JOIN pv.medicamento m " +
           "WHERE m.principioActivo = :principio " +
           "AND m.esBioequivalente = true " +
           "ORDER BY pv.precioMaxVta ASC")
    List<PrecioVigente> buscarBioequivalentesConPrecio(@Param("principio") String principio);
}