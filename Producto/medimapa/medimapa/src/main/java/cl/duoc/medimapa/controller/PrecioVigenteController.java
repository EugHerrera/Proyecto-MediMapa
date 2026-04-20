package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
public class PrecioVigenteController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    @Autowired
    private ScraperService scraperService;

    // Endpoint original para traer todo de la BD
    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        return precioRepo.findAll();
    }

    // 🔥 NUEVO: Endpoint para buscar CUALQUIER medicamento en vivo en las 3 farmacias
    @GetMapping("/buscar")
    public List<Map<String, Object>> buscarEnVivo(@RequestParam String query) {
        System.out.println("🔎 Frontend solicitó buscar: " + query);
        
        // Llamamos al servicio para que encienda Playwright y compare
        return scraperService.compararEnVivo(query);
    }
}
