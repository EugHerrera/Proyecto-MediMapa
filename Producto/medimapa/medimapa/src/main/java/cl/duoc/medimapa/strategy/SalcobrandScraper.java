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
        return 3L; // ID 3 para Salcobrand
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
        // Aplicamos el "Modo Dios" que tan bien nos funcionó con Dr. Simi
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
                        
                        // Ignoramos precios menores a $100 (basura o centavos)
                        if (actual.compareTo(new BigDecimal(100)) > 0) {
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