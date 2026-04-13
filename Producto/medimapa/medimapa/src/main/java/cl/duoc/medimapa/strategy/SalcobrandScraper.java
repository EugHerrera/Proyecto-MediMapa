package cl.duoc.medimapa.strategy;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SalcobrandScraper implements FarmaciaScraper {

    @Override
    public Long getIdFuente() {
        return 3L; // ID 3 para Salcobrand en tu BD
    }

    @Override
    public String getNombreFarmacia() {
        return "Farmacias Salcobrand";
    }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            String busquedaEncoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            // Salcobrand usa esta estructura para sus búsquedas
            return "https://salcobrand.cl/search_result?query=" + busquedaEncoded;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        // 1. Le damos 5 segundos para que cargue la página React de Salcobrand
        page.waitForTimeout(5000); 

        // 2. Volvemos al Modo Dios: leemos TODO lo que tenga un signo $
        Locator precios = page.locator("span, div, p").filter(new Locator.FilterOptions().setHasText("$"));
        
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;

            for (String t : textos) {
                // Limpiamos todo lo que no sea número
                String limpio = t.replaceAll("[^\\d]", "");
                if (!limpio.isEmpty()) {
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        int valor = actual.intValue(); // Lo pasamos a entero para filtrarlo fácil
                        
                        // 3. EL ESCUDO: Mayor a 100 y que NO sea parte del menú lateral falso
                        if (valor > 100 && valor != 5000 && valor != 10000 && valor != 15000 && valor != 20000 && valor != 25000) {
                            if (minPrecio == null || actual.compareTo(minPrecio) < 0) {
                                minPrecio = actual;
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar si no se pudo parsear
                    }
                }
            }
            return minPrecio;
        }
        return null;
    }
}