package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    // Consulta: SELECT * FROM medicamento WHERE principio_activo = ? AND es_bioequivalente = true
    List<Medicamento> findByPrincipioActivoAndEsBioequivalenteTrue(String principioActivo);
}