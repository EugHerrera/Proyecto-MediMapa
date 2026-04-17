package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.Medicamento;
import cl.duoc.medimapa.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@CrossOrigin(origins = "*") // Para que Sebastián no tenga problemas de CORS
public class MedicamentoController {

    @Autowired
    private MedicamentoRepository repository;

    // Obtener todos los medicamentos (El diccionario clínico completo)
    @GetMapping
    public ResponseEntity<List<Medicamento>> getAll() {
        List<Medicamento> medicamentos = repository.findAll();
        
        if (medicamentos.isEmpty()) {
            return ResponseEntity.noContent().build(); // Devuelve 204 si la tabla está vacía
        }
        return ResponseEntity.ok(medicamentos); // Devuelve 200 con la lista
    }

    // Buscador crudo por nombre o principio activo (Ideal para un mantenedor o panel Admin)
    @GetMapping("/buscar")
    public ResponseEntity<List<Medicamento>> buscar(@RequestParam String termino) {
        
        // 1. Intentamos buscar por el nombre oficial
        List<Medicamento> resultados = repository.findByNombreCanonicoContainingIgnoreCase(termino);
        
        // 2. Si no pilla nada, buscamos por la familia química (principio activo)
        if (resultados.isEmpty()) {
            resultados = repository.findByPrincipioActivoContainingIgnoreCase(termino);
        }
        
        // 3. Evaluamos si encontramos algo
        if (resultados.isEmpty()) {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found si no existe
        }
        
        return ResponseEntity.ok(resultados); // Devuelve 200 OK con los resultados
    }
}