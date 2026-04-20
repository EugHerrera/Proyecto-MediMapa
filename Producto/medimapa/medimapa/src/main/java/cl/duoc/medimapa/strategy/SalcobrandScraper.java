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
        return 3L; 
    }

    @Override
    public String getNombreFarmacia() {
        return "Farmacias Salcobrand";
    }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            String busquedaEncoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            return "https://salcobrand.cl/search_result?query=" + busquedaEncoded;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        page.waitForTimeout(5000); 

        // 🔥 MODO FRANCOTIRADOR: Evitamos agarrar el precio por pastilla suelta
        Locator precios = page.locator("[class*='price'], [class*='Precio'], .bestPrice").filter(new Locator.FilterOptions().setHasText("$"));
        
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;

            for (String t : textos) {
                // Separamos si viene basura pegada al número
                String primerTexto = t.split("[\\n\\s]+")[0];
                String limpio = primerTexto.replaceAll("[^\\d]", "");
                
                if (!limpio.isEmpty()) {
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        int valor = actual.intValue(); 
                        
                        // Ignoramos el menú lateral falso y valores ridículamente bajos (menos de $550)
                        if (valor > 550 && valor != 5000 && valor != 10000 && valor != 15000 && valor != 20000 && valor != 25000) {
                            if (minPrecio == null || actual.compareTo(minPrecio) < 0) {
                                minPrecio = actual;
                            }
                        }
                    } catch (Exception e) {
                        // Ignorar
                    }
                }
            }
            return minPrecio;
        }
        return null;
    }
}