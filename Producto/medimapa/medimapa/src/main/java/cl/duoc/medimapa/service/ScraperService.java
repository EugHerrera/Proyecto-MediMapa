package cl.duoc.medimapa.service;

import com.microsoft.playwright.*;
import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.strategy.FarmaciaScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ScraperService {

    @Autowired
    private PrecioVigenteRepository precioVigenteRepository;
    
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private SucursalFarmaciaRepository sucursalRepo; // Añadido para buscar la sucursal

    @Autowired
    private List<FarmaciaScraper> estrategiasFarmacias;

    public void ejecutarScrapingAutomatico(CorridaActualizacion corrida) {
        // El robot lee el catálogo completo desde la base de datos
        List<Medicamento> catalogo = medicamentoRepository.findAll();
        System.out.println("🚀 Iniciando motor Playwright Multi-Farmacia...");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            for (Medicamento medicamento : catalogo) {
                System.out.println("\n🤖 Procesando: " + medicamento.getNombre_canonico());

                // El robot recorre cada farmacia que tengamos programada
                for (FarmaciaScraper estrategia : estrategiasFarmacias) {
                    try {
                        System.out.println("   🏢 Buscando en: " + estrategia.getNombreFarmacia());
                        
                        // BUSCAMOS LA SUCURSAL CORRECTA EN LA BD
                        SucursalFarmacia sucursalCorrecta = sucursalRepo.findById(estrategia.getIdFuente()).orElse(null);
                        
                        if (sucursalCorrecta == null) {
                            System.out.println("      ⚠️ Error: No existe la sucursal ID " + estrategia.getIdFuente() + " en la base de datos.");
                            continue; // Saltamos a la siguiente estrategia si no hay sucursal
                        }

                        // Navegamos y extraemos
                        String url = estrategia.generarUrl(medicamento.getNombre_canonico());
                        page.navigate(url);
                        page.waitForTimeout(5000); 

                        BigDecimal precioEncontrado = estrategia.extraerMenorPrecio(page);

                        if (precioEncontrado != null) {
                            // GUARDAMOS USANDO LA SUCURSAL CORRECTA
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
}