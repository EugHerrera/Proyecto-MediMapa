package cl.duoc.medimapa.service;

import cl.duoc.medimapa.model.Medicamento;
import cl.duoc.medimapa.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BioequivalenciaService {

    @Autowired
    private MedicamentoRepository medicamentoRepo;

    public List<Medicamento> buscarAlternativas(String principioActivo) {
        // Ahora filtramos estrictamente por bioequivalencia certificada
        return medicamentoRepo.findByPrincipioActivoAndEsBioequivalenteTrue(principioActivo);
    }
}