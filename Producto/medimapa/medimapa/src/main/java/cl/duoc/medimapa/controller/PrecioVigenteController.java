package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.model.SucursalFarmacia;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import cl.duoc.medimapa.repository.SucursalFarmaciaRepository;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
@CrossOrigin(origins = "*") 
public class PrecioVigenteController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    @Autowired
    private ScraperService scraperService;

    // 🔥 TRAEMOS EL REPOSITORIO DE TODAS TUS SUCURSALES
    @Autowired
    private SucursalFarmaciaRepository sucursalRepo;

    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        return precioRepo.findAll();
    }

    @GetMapping("/buscar")
    public List<Map<String, Object>> buscarEnVivo(@RequestParam String query) {
        System.out.println("\n🔎 Frontend solicitó buscar: " + query);
        
        List<Map<String, Object>> preciosBase = new ArrayList<>();

        // 1. REVISAR LA BD (Caché)
        List<PrecioVigente> preciosEnBaseDeDatos = precioRepo.buscarPorNombreMedicamento(query);
        
        if (!preciosEnBaseDeDatos.isEmpty()) {
            System.out.println("⚡ Obteniendo precios base desde PostgreSQL...");
            for (PrecioVigente pv : preciosEnBaseDeDatos) {
                Map<String, Object> dato = new HashMap<>();
                String nombreFarmacia = (pv.getSucursal() != null) ? pv.getSucursal().getNombre_sucursal() : "Farmacia Asociada";
                dato.put("farmacia", nombreFarmacia);
                dato.put("precio", pv.getPrecio_max_vta());
                dato.put("medicamento", pv.getId().getTexto_busqueda());
                if (pv.getMedicamento() != null) {
                    dato.put("esBioequivalente", pv.getMedicamento().getEs_bioequivalente());
                }
                preciosBase.add(dato);
            }
        } else {
            // 2. MODO TURBO (Playwright)
            preciosBase = scraperService.compararEnVivo(query);
        }

        // 🔥 3. LA MAGIA: EXPANSIÓN A TODAS LAS SUCURSALES DE LA FLORIDA
        List<SucursalFarmacia> todasLasSucursales = sucursalRepo.findAll();
        List<Map<String, Object>> respuestaExpandida = new ArrayList<>();

        for (SucursalFarmacia sucursal : todasLasSucursales) {
            String nombreSuc = sucursal.getNombre_sucursal().toLowerCase();
            
            for (Map<String, Object> p : preciosBase) {
                String farmaciaPrecio = ((String) p.get("farmacia")).toLowerCase();
                
                boolean mismaCadena = false;
                if (nombreSuc.contains("ahumada") && farmaciaPrecio.contains("ahumada")) mismaCadena = true;
                else if (nombreSuc.contains("salco") && farmaciaPrecio.contains("salco")) mismaCadena = true;
                else if (nombreSuc.contains("simi") && farmaciaPrecio.contains("simi")) mismaCadena = true;
                else if (!nombreSuc.contains("ahumada") && !nombreSuc.contains("salco") && !nombreSuc.contains("simi") && 
                         !farmaciaPrecio.contains("ahumada") && !farmaciaPrecio.contains("salco") && !farmaciaPrecio.contains("simi")) {
                    mismaCadena = true; // Farmacias Independientes
                }

                if (mismaCadena) {
                    // Clonamos el precio y el bioequivalente, pero le ponemos la ubicación real de ESTA sucursal
                    Map<String, Object> datoExpandido = new HashMap<>(p);
                    datoExpandido.put("farmacia", sucursal.getNombre_sucursal());
                    datoExpandido.put("lat", sucursal.getLatitud());
                    datoExpandido.put("lng", sucursal.getLongitud());
                    respuestaExpandida.add(datoExpandido);
                    break; // Pasamos a la siguiente sucursal
                }
            }
        }
        
        System.out.println("🗺️ Expandiendo " + preciosBase.size() + " precios a " + respuestaExpandida.size() + " sucursales en el mapa.");
        
        // Si por alguna razón la BD de sucursales está vacía, devolvemos los 4 originales
        return respuestaExpandida.isEmpty() ? preciosBase : respuestaExpandida;
    }
}