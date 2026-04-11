package cl.duoc.medimapa.strategy;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AhumadaScraper implements FarmaciaScraper {

    @Override
    public Long getIdFuente() {
        return 1L; // Asumiendo que Ahumada es el ID 1 en tu BD
    }

    @Override
    public String getNombreFarmacia() {
        return "Farmacias Ahumada";
    }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            String busquedaEncoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            return "https://www.farmaciasahumada.cl/search?q=" + busquedaEncoded;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        Locator precios = page.locator(".price, .price-wrapper").filter(new Locator.FilterOptions().setHasText("$"));
        
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;

            for (String t : textos) {
                String limpio = t.split("\n")[0].replaceAll("[^\\d]", "");
                if (!limpio.isEmpty()) {
                    BigDecimal actual = new BigDecimal(limpio);
                    if (actual.compareTo(new BigDecimal(100)) > 0) {
                        if (minPrecio == null || actual.compareTo(minPrecio) < 0) minPrecio = actual;
                    }
                }
            }
            return minPrecio;
        }
        return null; // Si no encuentra nada, devuelve nulo
    }
}