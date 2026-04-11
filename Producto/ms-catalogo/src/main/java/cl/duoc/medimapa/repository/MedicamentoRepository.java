package cl.duoc.medimapa.repository;

import cl.duoc.medimapa.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    // Para el buscador principal: buscar por nombre (ej: "Aspirina")
    List<Medicamento> findByNombreCanonicoContainingIgnoreCase(String nombre);
    
    // Para la bioequivalencia: buscar por principio activo (ej: "Ácido Acetilsalicílico")
    List<Medicamento> findByPrincipioActivoContainingIgnoreCase(String principio);
}