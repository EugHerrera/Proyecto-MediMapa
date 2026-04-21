package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.dto.MedicamentoResponseDTO;
import cl.duoc.medimapa.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService servicio;

    @GetMapping("/buscar")
    public List<MedicamentoResponseDTO> buscar(@RequestParam String query) {
        return servicio.buscarMedicamentos(query);
    }

    @GetMapping("/bioequivalentes")
    public List<MedicamentoResponseDTO> buscarBioequivalentes(@RequestParam String principioActivo) {
        return servicio.obtenerBioequivalentes(principioActivo);
    }
}