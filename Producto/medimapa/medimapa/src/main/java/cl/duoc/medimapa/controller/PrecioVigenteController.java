package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
public class PrecioVigenteController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    @Autowired
    private ScraperService scraperService;

    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        return precioRepo.findAll();
    }

    // 🔥 EL GUARDIA DE TRÁNSITO INTELIGENTE (Estrategia Caché-First)
    @GetMapping("/buscar")
    public List<Map<String, Object>> buscarEnVivo(@RequestParam String query) {
        System.out.println("\n🔎 Frontend solicitó buscar: " + query);
        
        // 1. REVISAR LA BASE DE DATOS PRIMERO (Respuesta en 0.1 segundos)
        List<PrecioVigente> preciosEnBaseDeDatos = precioRepo.buscarPorNombreMedicamento(query);
        
        if (!preciosEnBaseDeDatos.isEmpty()) {
            System.out.println("⚡ ¡Medicamento encontrado en Caché (PostgreSQL)! Devolviendo al instante.");
            
            // Transformamos la data de BD al formato que React ya entiende
            List<Map<String, Object>> respuestaRapida = new ArrayList<>();
            for (PrecioVigente pv : preciosEnBaseDeDatos) {
                Map<String, Object> dato = new HashMap<>();
                
                // Evitamos errores si por alguna razón la sucursal viene nula
                String nombreFarmacia = (pv.getSucursal() != null) ? pv.getSucursal().getNombre_sucursal() : "Farmacia Asociada";
                
                dato.put("farmacia", nombreFarmacia);
                dato.put("precio", pv.getPrecio_max_vta());
                dato.put("medicamento", pv.getId().getTexto_busqueda());
                
                respuestaRapida.add(dato);
            }
            return respuestaRapida;
        }

        // 2. SI NO ESTÁ EN BD, ENCENDEMOS EL ROBOT (Respuesta en 13 segundos)
        System.out.println("🐢 No está en caché. Encendiendo a Playwright en Modo Turbo...");
        return scraperService.compararEnVivo(query);
    }
}