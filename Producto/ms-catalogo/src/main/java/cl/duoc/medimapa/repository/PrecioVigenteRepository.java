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

    // Cambiamos a LOWER para que no importe si escribes paracetamol o PARACETAMOL
    // Y quitamos temporalmente el filtro de bioequivalente para asegurar que veas datos
    @Query("SELECT pv FROM PrecioVigente pv " +
           "JOIN pv.medicamento m " +
           "WHERE LOWER(m.principioActivo) = LOWER(:principio) " +
           "OR LOWER(m.nombreCanonico) = LOWER(:principio) " +
           "ORDER BY pv.precioMaxVta ASC")
    List<PrecioVigente> buscarBioequivalentesConPrecio(@Param("principio") String principio);
}