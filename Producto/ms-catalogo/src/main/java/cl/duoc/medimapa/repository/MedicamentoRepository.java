package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    List<Medicamento> findByActivoTrue();

    List<Medicamento> findByCategoriaAndActivoTrue(String categoria);

    @Query("SELECT m FROM Medicamento m WHERE m.activo = true AND " +
           "(LOWER(m.nombreCanonico) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(m.principioActivo) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Medicamento> buscadorGeneral(@Param("q") String q);
}