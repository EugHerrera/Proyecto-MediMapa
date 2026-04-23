package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    // 🔥 Agregamos LOWER para que encuentre el nombre aunque cambien las mayúsculas
    @Query("SELECT m FROM Medicamento m WHERE LOWER(m.nombre_canonico) = LOWER(:nombre)")
    Optional<Medicamento> findByNombreCanonico(@Param("nombre") String nombre);
}
