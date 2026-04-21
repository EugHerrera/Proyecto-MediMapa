package cl.duoc.medimapa.service;

import cl.duoc.medimapa.dto.MedicamentoResponseDTO;
import cl.duoc.medimapa.model.Medicamento;
import cl.duoc.medimapa.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository repo;

    public List<MedicamentoResponseDTO> buscarMedicamentos(String query) {
        return repo.buscarPorNombre(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicamentoResponseDTO> obtenerBioequivalentes(String principio) {
        return repo.findByPrincipioActivoAndEsBioequivalenteTrue(principio).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MedicamentoResponseDTO convertToDTO(Medicamento med) {
        MedicamentoResponseDTO dto = new MedicamentoResponseDTO();
        dto.setIdMedicamento(med.getIdMedicamento());
        dto.setNombreCanonico(med.getNombreCanonico());
        dto.setPrincipioActivo(med.getPrincipioActivo());
        dto.setEsBioequivalente(med.getEsBioequivalente());
        return dto;
    }
}