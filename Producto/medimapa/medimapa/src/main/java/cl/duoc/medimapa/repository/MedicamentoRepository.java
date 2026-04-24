package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    // 🔥 Búsqueda "Blindada": Usa LOWER para ignorar mayúsculas y minúsculas
    @Query("SELECT m FROM Medicamento m WHERE LOWER(m.nombre_canonico) = LOWER(:nombre)")
    Optional<Medicamento> findByNombreCanonico(@Param("nombre") String nombre);
}