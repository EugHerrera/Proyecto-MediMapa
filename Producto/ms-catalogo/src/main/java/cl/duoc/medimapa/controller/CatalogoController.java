package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.MedicamentoCatalogo;
import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.MedicamentoCatalogoRepository;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*") // <-- ¡La magia contra el bloqueo! Permite peticiones de cualquier frontend
public class CatalogoController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    // Inyectamos nuestro nuevo repositorio traductor
    @Autowired
    private MedicamentoCatalogoRepository catalogoRepo;

    // Endpoint original que ya tenías
    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        return precioRepo.findAll();
    }

    // NUEVO ENDPOINT: La solución al problema de Eugenio
    // Ruta: GET http://localhost:8081/api/catalogo/traducir?nombre=Tapsin
    @GetMapping("/traducir")
    public ResponseEntity<?> traducirMedicamento(@RequestParam String nombre) {
        
        Optional<MedicamentoCatalogo> medicamento = catalogoRepo.findByNombreComercialIgnoreCase(nombre);
        
        if (medicamento.isPresent()) {
            // Si encuentra "Tapsin", devuelve el JSON con "Paracetamol"
            return ResponseEntity.ok(medicamento.get());
        } else {
            // Si el usuario busca algo que no está en la base de datos
            return ResponseEntity.notFound().build();
        }
    }
}