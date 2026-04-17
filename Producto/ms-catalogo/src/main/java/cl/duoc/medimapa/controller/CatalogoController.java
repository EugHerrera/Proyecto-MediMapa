package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.MedicamentoCatalogo;
import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.MedicamentoCatalogoRepository;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    @Autowired
    private MedicamentoCatalogoRepository catalogoRepo;

    @GetMapping("/traducir")
    public ResponseEntity<?> buscarMedicamentoCompleto(@RequestParam String nombre) {
        
        // 1. Traducimos (ej: Tapsin -> Paracetamol)
        Optional<MedicamentoCatalogo> traduccion = catalogoRepo.findByNombreComercialIgnoreCase(nombre);
        
        if (traduccion.isPresent()) {
            String principio = traduccion.get().getPrincipioActivo();
            
            // 2. Buscamos PRECIOS y SUCURSALES de los bioequivalentes
            List<PrecioVigente> resultados = precioRepo.buscarBioequivalentesConPrecio(principio);
            
            // 3. Armamos la respuesta completa para el mapa y las tarjetas
            Map<String, Object> response = new HashMap<>();
            response.put("termino_buscado", nombre);
            response.put("principio_activo", principio);
            response.put("total_encontrados", resultados.size());
            response.put("resultados", resultados);
            
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.notFound().build();
    }
}