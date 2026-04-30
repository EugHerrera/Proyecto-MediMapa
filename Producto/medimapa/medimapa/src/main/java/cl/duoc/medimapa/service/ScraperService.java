package cl.duoc.medimapa.service;

import com.microsoft.playwright.*;
import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.strategy.FarmaciaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScraperService {

    @Autowired private PrecioVigenteRepository precioVigenteRepository;
    @Autowired private MedicamentoRepository medicamentoRepository;
    @Autowired private SucursalFarmaciaRepository sucursalRepo;
    @Autowired private List<FarmaciaScraper> estrategiasFarmacias;

    public void ejecutarScrapingAutomatico(CorridaActualizacion corrida) {
        List<Medicamento> catalogo = medicamentoRepository.findAll();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            for (Medicamento medicamento : catalogo) {
                for (FarmaciaScraper estrategia : estrategiasFarmacias) {
                    try {
                        List<SucursalFarmacia> sucursales = sucursalRepo.findByFarmacia_Id(estrategia.getIdFuente());
                        if (sucursales.isEmpty()) continue;

                        page.navigate(estrategia.generarUrl(medicamento.getNombre_canonico()));
                        page.waitForTimeout(3000); 
                        
                        // 🔥 Pasamos el nombre para que el scraper valide
                        BigDecimal precio = estrategia.extraerMenorPrecio(page, medicamento.getNombre_canonico());
                        
                        if (precio != null) {
                            for (SucursalFarmacia sucursal : sucursales) {
                                PrecioVigente pv = new PrecioVigente();
                                pv.setTextoBusqueda(medicamento.getNombre_canonico());
                                pv.setPrecio_max_vta(precio);
                                pv.setMoneda("CLP");
                                pv.setVigente_desde(OffsetDateTime.now());
                                pv.setSucursal(sucursal);
                                pv.setMedicamento(medicamento);
                                pv.setCorrida(corrida);
                                
                                precioVigenteRepository.save(pv);
                            }
                        }
                    } catch (Exception e) { 
                        System.err.println("Error en Batch para " + estrategia.getNombreFarmacia()); 
                    }
                }
            }
        } catch (Exception e) {}
    }

    public List<Map<String, Object>> compararEnVivo(String terminoBusqueda) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            
            page.route("**/*", route -> {
                String tipo = route.request().resourceType();
                if ("stylesheet".equals(tipo) || "font".equals(tipo) || "media".equals(tipo)) {
                    route.abort(); 
                } else {
                    route.resume(); 
                }
            });

            for (FarmaciaScraper estrategia : estrategiasFarmacias) {   
                try {
                    page.navigate(estrategia.generarUrl(terminoBusqueda));
                    page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
                    
                    // 🔥 Pasamos la búsqueda exacta
                    BigDecimal precio = estrategia.extraerMenorPrecio(page, terminoBusqueda);
                    
                    if (precio != null) {
                        boolean esBio = estrategia.esBioequivalente(page);
                        Map<String, Object> dato = new HashMap<>();
                        dato.put("farmacia", estrategia.getNombreFarmacia());
                        dato.put("precio", precio);
                        dato.put("medicamento", terminoBusqueda);
                        dato.put("esBioequivalente", esBio); 
                        resultados.add(dato);
                    }
                } catch (Exception e) {}
            }
        } catch (Exception e) {}
        return resultados;
    }
}