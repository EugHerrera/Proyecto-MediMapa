package cl.duoc.medimapa.controller;
import cl.duoc.medimapa.dto.MedicamentoResponseDTO;
import cl.duoc.medimapa.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "http://localhost:5173") // <-- ESTA ES LA LLAVE QUE DEJA ENTRAR A REACT
public class MedicamentoController {

    @Autowired
    private MedicamentoService servicio;

    @GetMapping
    public List<MedicamentoResponseDTO> listarTodos() {
        return servicio.obtenerTodo();
    }

    @GetMapping("/buscar")
    public List<MedicamentoResponseDTO> buscar(@RequestParam String q) {
        return servicio.buscar(q);
    }

    @GetMapping("/categoria")
    public List<MedicamentoResponseDTO> porCategoria(@RequestParam String nombre) {
        return servicio.filtrarPorCategoria(nombre);
    }
}