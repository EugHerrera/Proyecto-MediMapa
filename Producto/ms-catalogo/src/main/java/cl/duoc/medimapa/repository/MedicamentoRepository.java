package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    // Para el flujo de bioequivalencia
    List<Medicamento> findByPrincipioActivoAndEsBioequivalenteTrue(String principioActivo);

    // Para el buscador de la enciclopedia médica (MedicamentoController)
    List<Medicamento> findByNombreCanonicoContainingIgnoreCase(String nombre);
    List<Medicamento> findByPrincipioActivoContainingIgnoreCase(String principio);
}