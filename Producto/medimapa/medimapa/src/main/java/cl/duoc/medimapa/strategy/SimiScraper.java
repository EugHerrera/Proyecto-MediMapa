package cl.duoc.medimapa.strategy;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SimiScraper implements FarmaciaScraper {

    @Override
    public Long getIdFuente() {
        return 2L; 
    }

    @Override
    public String getNombreFarmacia() {
        return "Farmacias Dr. Simi";
    }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            String busquedaEncoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            return "https://www.drsimi.cl/" + busquedaEncoded + "?_q=" + busquedaEncoded + "&map=ft&order=OrderByPriceASC";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        // 🔥 MODO FRANCOTIRADOR: Solo elementos que en su código se llamen "price"
        Locator precios = page.locator("[class*='price'], [class*='Precio']").filter(new Locator.FilterOptions().setHasText("$"));
        
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;

            for (String t : textos) {
                // 🔥 EL ARREGLO: Cortamos el texto al primer espacio o salto de línea para separar precios pegados
                String primerTexto = t.split("[\\n\\s]+")[0]; 
                String limpio = primerTexto.replaceAll("[^\\d]", "");
                
                if (!limpio.isEmpty()) {
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        
                        if (actual.compareTo(new BigDecimal(100)) > 0) {
                            if (minPrecio == null || actual.compareTo(minPrecio) < 0) {
                                minPrecio = actual;
                            }
                        }
                    } catch (Exception e) {
                        // Ignorado
                    }
                }
            }
            return minPrecio;
        }
        return null;
    }
}