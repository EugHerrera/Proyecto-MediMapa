package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
@CrossOrigin(origins = "*")
public class PrecioVigenteController {

    @Autowired private PrecioVigenteRepository precioRepo;
    @Autowired private ScraperService scraperService;
    @Autowired private SucursalFarmaciaRepository sucursalRepo;
    @Autowired private MedicamentoRepository medicamentoRepo;
    @Autowired private CorridaActualizacionRepository corridaRepo;

    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        return precioRepo.findAll();
    }

    @GetMapping("/buscar")
    @Transactional 
    public List<Map<String, Object>> buscarEnVivo(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "false") boolean forceRefresh) {
        
        System.out.println("\n🔎 Frontend solicitó buscar: " + query + " | Forzar Refresh: " + forceRefresh);

        if (!forceRefresh) {
            List<PrecioVigente> preciosEnBD = precioRepo.buscarPorNombreMedicamento(query);
            if (!preciosEnBD.isEmpty()) {
                System.out.println("⚡ Obteniendo precios cacheados desde PostgreSQL...");
                List<Map<String, Object>> respuestaCacheada = new ArrayList<>();
                
                for (PrecioVigente pv : preciosEnBD) {
                    Map<String, Object> dato = new HashMap<>();
                    dato.put("farmacia", pv.getSucursal() != null ? pv.getSucursal().getNombre_sucursal() : "Desconocida");
                    dato.put("precio", pv.getPrecio_max_vta());
                    // 🔥 Aquí extraemos el texto correctamente
                    dato.put("medicamento", pv.getMedicamento() != null ? pv.getMedicamento().getNombre_canonico() : pv.getTextoBusqueda());
                    
                    if (pv.getMedicamento() != null) {
                        dato.put("esBioequivalente", pv.getMedicamento().getEs_bioequivalente());
                    }
                    if (pv.getSucursal() != null && pv.getSucursal().getUbicacion() != null) {
                        dato.put("lat", pv.getSucursal().getUbicacion().getY());
                        dato.put("lng", pv.getSucursal().getUbicacion().getX());
                    }
                    respuestaCacheada.add(dato);
                }
                return respuestaCacheada; 
            }
        }

        System.out.println("🚀 INICIANDO MODO TURBO (Playwright) para buscar precios frescos...");
        List<Map<String, Object>> preciosNacionales = scraperService.compararEnVivo(query);

        if (preciosNacionales.isEmpty()) {
            System.out.println("❌ No se encontraron resultados en la web.");
            return new ArrayList<>();
        }

        List<SucursalFarmacia> todasLasSucursales = sucursalRepo.findAll();
        List<Map<String, Object>> respuestaExpandida = new ArrayList<>();

        for (Map<String, Object> precioScrapeado : preciosNacionales) {
            String nombreCadenaScrapeada = (String) precioScrapeado.get("farmacia");

            for (SucursalFarmacia sucursal : todasLasSucursales) {
                if (sucursal.getFarmacia() != null && 
                    sucursal.getFarmacia().getNombre().equalsIgnoreCase(nombreCadenaScrapeada)) {
                    
                    Map<String, Object> datoExpandido = new HashMap<>(precioScrapeado);
                    datoExpandido.put("cadenaFarmacia", sucursal.getFarmacia().getNombre());
                    datoExpandido.put("farmacia", sucursal.getNombre_sucursal()); 
                    
                    if (sucursal.getUbicacion() != null) {
                        datoExpandido.put("lat", sucursal.getUbicacion().getY());
                        datoExpandido.put("lng", sucursal.getUbicacion().getX());
                    }
                    
                    datoExpandido.put("sucursalObj", sucursal); 
                    respuestaExpandida.add(datoExpandido);
                }
            }
        }

        System.out.println("🗑️ Borrando basura antigua de caché para: " + query);
        List<PrecioVigente> basuraAntigua = precioRepo.buscarPorNombreMedicamento(query);
        precioRepo.deleteAll(basuraAntigua);
        precioRepo.flush(); 

        System.out.println("💾 Guardando los nuevos precios en la Base de Datos...");
        CorridaActualizacion corrida = new CorridaActualizacion();
        corrida.setId_fuente(0L); 
        corrida.setInicio(java.time.OffsetDateTime.now());
        corrida.setEstado("ok");
        corrida.setFin(java.time.OffsetDateTime.now());
        corrida = corridaRepo.save(corrida);

        for (Map<String, Object> dato : respuestaExpandida) {
            SucursalFarmacia sucursal = (SucursalFarmacia) dato.get("sucursalObj");
            String nombreMed = (String) dato.get("medicamento");
            BigDecimal precio = (BigDecimal) dato.get("precio");
            boolean esBio = dato.containsKey("esBioequivalente") && (boolean) dato.get("esBioequivalente");

            Medicamento med = medicamentoRepo.findByNombreCanonico(nombreMed).orElse(null);
            if (med == null) {
                med = new Medicamento();
                med.setNombre_canonico(nombreMed);
                med.setPrincipio_activo(nombreMed);
                med.setOrigen_catalogo("SCRAPER_VIVO");
                med.setEs_bioequivalente(esBio);
                med = medicamentoRepo.save(med);
            } else if (esBio && !med.getEs_bioequivalente()) {
                med.setEs_bioequivalente(true);
                medicamentoRepo.save(med);
            }

            PrecioVigente pv = new PrecioVigente();
            // 🔥 GUARDADO LIMPIO: Sin llaves compuestas
            pv.setTextoBusqueda(query); 
            pv.setSucursal(sucursal);
            pv.setMedicamento(med);
            pv.setPrecio_max_vta(precio);
            pv.setMoneda("CLP");
            pv.setVigente_desde(java.time.OffsetDateTime.now());
            pv.setCorrida(corrida);

            precioRepo.save(pv);
        }
        
        respuestaExpandida.forEach(map -> map.remove("sucursalObj"));

        System.out.println("✅ Actualización completada sin errores de SQL.");
        return respuestaExpandida;
    }
}