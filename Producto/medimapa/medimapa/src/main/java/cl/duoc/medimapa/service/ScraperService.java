package cl.duoc.medimapa.service;

import com.microsoft.playwright.*;
import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class ScraperService {

    @Autowired
    private PrecioVigenteRepository precioVigenteRepository;

    public void extraerYGuardarPrecio(String url, String textoBusqueda, SucursalFarmacia sucursal, Medicamento medicamento, CorridaActualizacion corrida) {
        System.out.println("🚀 Iniciando motor Playwright...");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate(url);
            page.waitForTimeout(6000); 

            Locator precioLocator = page.locator(".price, .price-wrapper").filter(new Locator.FilterOptions().setHasText("$")).first();
            
            if (precioLocator.count() > 0) {
                String precioTexto = precioLocator.innerText().split("\n")[0].trim();
                String soloNumeros = precioTexto.replaceAll("[^\\d]", "");
                
                if (!soloNumeros.isEmpty()) {
                    BigDecimal precioFinal = new BigDecimal(soloNumeros);

                    // ARMAMOS EL OBJETO CON TODAS SUS RELACIONES
                    PrecioVigente nuevoPrecio = new PrecioVigente();
                    
                    PrecioVigenteId idCompuesto = new PrecioVigenteId();
                    idCompuesto.setId_sucursal(sucursal.getId_sucursal());
                    idCompuesto.setTexto_busqueda(textoBusqueda);
                    
                    nuevoPrecio.setId(idCompuesto);
                    nuevoPrecio.setPrecio_max_vta(precioFinal);
                    nuevoPrecio.setMoneda("CLP");
                    nuevoPrecio.setVigente_desde(OffsetDateTime.now());
                    
                    // ASIGNAMOS LOS OBJETOS PADRES (Esto evita el error que tenías)
                    nuevoPrecio.setSucursal(sucursal);
                    nuevoPrecio.setMedicamento(medicamento);
                    nuevoPrecio.setCorrida(corrida);

                    precioVigenteRepository.save(nuevoPrecio);

                    System.out.println("\n======================================");
                    System.out.println("✅ ¡HOME RUN! GUARDADO EXITOSO");
                    System.out.println("💊 Medicamento: " + medicamento.getNombre_canonico());
                    System.out.println("💰 Precio: $" + precioFinal);
                    System.out.println("======================================\n");
                }
            }
            browser.close();
        } catch (Exception e) {
            System.err.println("Error técnico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}