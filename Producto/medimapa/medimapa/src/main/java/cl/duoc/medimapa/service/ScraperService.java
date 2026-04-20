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

    @Autowired
    private PrecioVigenteRepository precioVigenteRepository;
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private SucursalFarmaciaRepository sucursalRepo;

    @Autowired
    private List<FarmaciaScraper> estrategiasFarmacias;

    // -------------------------------------------------------------------------
    // MÉTODO 1: Scraping Masivo Programado (Batch)
    // -------------------------------------------------------------------------
    public void ejecutarScrapingAutomatico(CorridaActualizacion corrida) {
        List<Medicamento> catalogo = medicamentoRepository.findAll();
        System.out.println("🚀 Iniciando motor Playwright Multi-Farmacia (Modo Batch)...");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            for (Medicamento medicamento : catalogo) {
                System.out.println("\n🤖 Procesando: " + medicamento.getNombre_canonico());

                for (FarmaciaScraper estrategia : estrategiasFarmacias) {
                    try {
                        System.out.println("   🏢 Buscando en: " + estrategia.getNombreFarmacia());
                        
                        SucursalFarmacia sucursalCorrecta = sucursalRepo.findById(estrategia.getIdFuente()).orElse(null);
                        
                        if (sucursalCorrecta == null) {
                            System.out.println("      ⚠️ Error: No existe la sucursal ID " + estrategia.getIdFuente() + " en la base de datos.");
                            continue; 
                        }

                        String url = estrategia.generarUrl(medicamento.getNombre_canonico());
                        page.navigate(url);
                        page.waitForTimeout(5000); 

                        BigDecimal precioEncontrado = estrategia.extraerMenorPrecio(page);

                        if (precioEncontrado != null) {
                            PrecioVigente pv = new PrecioVigente();
                            PrecioVigenteId id = new PrecioVigenteId();
                            
                            id.setId_sucursal(sucursalCorrecta.getId_sucursal()); 
                            id.setTexto_busqueda(medicamento.getNombre_canonico());
                            
                            pv.setId(id);
                            pv.setPrecio_max_vta(precioEncontrado);
                            pv.setMoneda("CLP");
                            pv.setVigente_desde(OffsetDateTime.now());
                            
                            pv.setSucursal(sucursalCorrecta);
                            pv.setMedicamento(medicamento);
                            pv.setCorrida(corrida);

                            precioVigenteRepository.save(pv);
                            System.out.println("      ✅ ¡Guardado en " + sucursalCorrecta.getNombre_sucursal() + "! $" + precioEncontrado);
                        } else {
                            System.out.println("      ⚠️ Sin stock o precio no encontrado.");
                        }

                    } catch (Exception e) {
                        System.err.println("      ❌ Falló búsqueda en " + estrategia.getNombreFarmacia());
                    }
                }
            }
            browser.close();
            System.out.println("\n🏁 Proceso de scraping masivo finalizado.");
        } catch (Exception e) {
            System.err.println("❌ Error crítico en el motor: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // MÉTODO 2: Scraping en Vivo para el Buscador del Frontend (MODO TURBO)
    // -------------------------------------------------------------------------
    public List<Map<String, Object>> compararEnVivo(String terminoBusqueda) {
        System.out.println("🚀 Arrancando Playwright (MODO TURBO) para comparar: " + terminoBusqueda);
        
        List<Map<String, Object>> resultados = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            // MODO SIGILOSO: Headless true para no abrir ventanas
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            // 🔥 FILTRO ACELERADOR: Bloquea descarga de imágenes y basura visual
            page.route("**/*", route -> {
                String tipo = route.request().resourceType();
                if ("image".equals(tipo) || "stylesheet".equals(tipo) || "font".equals(tipo) || "media".equals(tipo)) {
                    route.abort(); 
                } else {
                    route.resume(); 
                }
            });

            for (FarmaciaScraper estrategia : estrategiasFarmacias) {   
                try {
                    System.out.println("   🏢 Buscando en: " + estrategia.getNombreFarmacia());
                    
                    String url = estrategia.generarUrl(terminoBusqueda);
                    
                    // NAVEGACIÓN INTELIGENTE: Solo espera a que cargue el HTML base, no espera 5 segundos fijos
                    page.navigate(url);
                    page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);

                    BigDecimal precioEncontrado = estrategia.extraerMenorPrecio(page);

                    if (precioEncontrado != null) {
                        System.out.println("      ✅ Encontrado a $" + precioEncontrado);
                        
                        Map<String, Object> dato = new HashMap<>();
                        dato.put("farmacia", estrategia.getNombreFarmacia());
                        dato.put("precio", precioEncontrado);
                        dato.put("medicamento", terminoBusqueda);
                        
                        resultados.add(dato);
                    } else {
                        System.out.println("      ⚠️ No encontrado en esta farmacia.");
                    }

                } catch (Exception e) {
                    System.err.println("      ❌ Error en la farmacia " + estrategia.getNombreFarmacia() + ": " + e.getMessage());
                }
            }
            browser.close();
            System.out.println("🏁 Búsqueda terminada. Devolviendo " + resultados.size() + " resultados al frontend.");
            
        } catch (Exception e) {
            System.err.println("❌ Error crítico en Playwright: " + e.getMessage());
        }

        return resultados;
    }
}