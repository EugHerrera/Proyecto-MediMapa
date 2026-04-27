package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; // 🔥 VITAL PARA BORRAR Y GUARDAR
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
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

    // 🔥 TRANSACTIONAL ASEGURA QUE SE BORRE Y GUARDE SIN ERRORES
    @GetMapping("/buscar")
    @Transactional 
    public List<Map<String, Object>> buscarEnVivo(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "false") boolean forceRefresh) {
        
        System.out.println("\n🔎 Frontend solicitó buscar: " + query + " | Forzar Refresh: " + forceRefresh);

        // =========================================================
        // CAMINO 1: MODO CACHÉ (Búsqueda Rápida sin botón naranja)
        // =========================================================
        if (!forceRefresh) {
            List<PrecioVigente> preciosEnBD = precioRepo.buscarPorNombreMedicamento(query);
            if (!preciosEnBD.isEmpty()) {
                System.out.println("⚡ Obteniendo precios cacheados desde PostgreSQL...");
                List<Map<String, Object>> respuestaCacheada = new ArrayList<>();
                
                for (PrecioVigente pv : preciosEnBD) {
                    Map<String, Object> dato = new HashMap<>();
                    dato.put("farmacia", pv.getSucursal() != null ? pv.getSucursal().getNombre_sucursal() : "Desconocida");
                    dato.put("precio", pv.getPrecio_max_vta());
                    dato.put("medicamento", pv.getMedicamento() != null ? pv.getMedicamento().getNombre_canonico() : pv.getId().getTexto_busqueda());
                    
                    if (pv.getMedicamento() != null) {
                        dato.put("esBioequivalente", pv.getMedicamento().getEs_bioequivalente());
                    }
                    if (pv.getSucursal() != null) {
                        dato.put("lat", pv.getSucursal().getLatitud());
                        dato.put("lng", pv.getSucursal().getLongitud());
                    }
                    respuestaCacheada.add(dato);
                }
                // RETORNO INMEDIATO: Ya está listo para React
                return respuestaCacheada; 
            }
        }

        // =========================================================
        // CAMINO 2: MODO TURBO (El robot trabaja en vivo)
        // =========================================================
        System.out.println("🚀 INICIANDO MODO TURBO (Playwright) para buscar precios frescos...");
        List<Map<String, Object>> preciosNacionales = scraperService.compararEnVivo(query);

        if (preciosNacionales.isEmpty()) {
            System.out.println("❌ No se encontraron resultados en la web.");
            return new ArrayList<>();
        }

        // EXPANSIÓN A SUCURSALES (Solo con los datos frescos del robot)
        List<SucursalFarmacia> todasLasSucursales = sucursalRepo.findAll();
        List<Map<String, Object>> respuestaExpandida = new ArrayList<>();

        for (SucursalFarmacia sucursal : todasLasSucursales) {
            String nombreSuc = sucursal.getNombre_sucursal().toLowerCase();
            for (Map<String, Object> p : preciosNacionales) {
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
                    Map<String, Object> datoExpandido = new HashMap<>(p);
                    datoExpandido.put("farmacia", sucursal.getNombre_sucursal());
                    datoExpandido.put("lat", sucursal.getLatitud());
                    datoExpandido.put("lng", sucursal.getLongitud());
                    datoExpandido.put("sucursalObj", sucursal); 
                    respuestaExpandida.add(datoExpandido);
                    break; 
                }
            }
        }

        // =========================================================
        // CAMINO 3: AUTOLIMPIEZA Y GUARDADO
        // =========================================================
        System.out.println("🗑️ Borrando basura antigua de caché para: " + query);
        List<PrecioVigente> basuraAntigua = precioRepo.buscarPorNombreMedicamento(query);
        precioRepo.deleteAll(basuraAntigua);
        precioRepo.flush(); // 🔥 Obligamos a PostgreSQL a borrar los datos viejos inmediatamente

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

            PrecioVigenteId pvId = new PrecioVigenteId();
            pvId.setId_sucursal(sucursal.getId_sucursal());
            
            // 🔥 Guardamos con el nombre exacto de la búsqueda del usuario
            pvId.setTexto_busqueda(query); 

            PrecioVigente pv = new PrecioVigente();
            pv.setId(pvId);
            pv.setSucursal(sucursal);
            pv.setMedicamento(med);
            pv.setPrecio_max_vta(precio);
            pv.setMoneda("CLP");
            pv.setVigente_desde(java.time.OffsetDateTime.now());
            pv.setCorrida(corrida);

            precioRepo.save(pv);
        }
        
        // Limpiamos la sucursal para evitar el error de JSON en React
        respuestaExpandida.forEach(map -> map.remove("sucursalObj"));

        System.out.println("✅ Actualización y Autolimpieza completadas con éxito.");
        return respuestaExpandida;
    }
}