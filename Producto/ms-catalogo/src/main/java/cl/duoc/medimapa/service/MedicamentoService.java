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

    public List<MedicamentoResponseDTO> obtenerTodo() {
        return repo.findByActivoTrue().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MedicamentoResponseDTO> filtrarPorCategoria(String categoria) {
        return repo.findByCategoriaAndActivoTrue(categoria).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<MedicamentoResponseDTO> buscar(String query) {
        return repo.buscadorGeneral(query).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private MedicamentoResponseDTO convertToDTO(Medicamento med) {
        MedicamentoResponseDTO dto = new MedicamentoResponseDTO();
        dto.setIdMedicamento(med.getIdMedicamento());
        dto.setNombreCanonico(med.getNombreCanonico());
        dto.setPrincipioActivo(med.getPrincipioActivo());
        dto.setCategoria(med.getCategoria());
        dto.setEsBioequivalente(med.getEsBioequivalente());
        return dto;
    }
}