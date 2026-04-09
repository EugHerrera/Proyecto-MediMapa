package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.Medicamento;
import cl.duoc.medimapa.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "*") // Para que Sebastián no tenga problemas de CORS
public class MedicamentoController {

    @Autowired
    private MedicamentoRepository repository;

    // Obtener todos los medicamentos
    @GetMapping
    public List<Medicamento> getAll() {
        return repository.findAll();
    }

    // Buscador por nombre o principio activo (Bioequivalencia)
    @GetMapping("/buscar")
    public List<Medicamento> buscar(@RequestParam String termino) {
        // Buscamos por ambos campos para dar una respuesta completa
        List<Medicamento> resultados = repository.findByNombreCanonicoContainingIgnoreCase(termino);
        if (resultados.isEmpty()) {
            resultados = repository.findByPrincipioActivoContainingIgnoreCase(termino);
        }
        return resultados;
    }
}