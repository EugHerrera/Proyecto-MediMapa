package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.Medicamento;
import cl.duoc.medimapa.service.BioequivalenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bioequivalencia")
@CrossOrigin(origins = "*") // Escudo contra bloqueos CORS
public class BioequivalenciaController {

    @Autowired
    private BioequivalenciaService bioService;

    // Endpoint: GET /api/bioequivalencia/buscar?principioActivo=Paracetamol
    @GetMapping("/buscar")
    public List<Medicamento> buscarBioequivalentes(@RequestParam String principioActivo) {
        return bioService.buscarAlternativas(principioActivo);
    }
}