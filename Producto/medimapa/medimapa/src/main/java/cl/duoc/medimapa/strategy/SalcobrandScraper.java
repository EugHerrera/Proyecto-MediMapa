
package cl.duoc.medimapa.strategy;

import com.microsoft.playwright.Page;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SalcobrandScraper implements FarmaciaScraper {
    @Override public Long getIdFuente() { return 3L; }
    @Override public String getNombreFarmacia() { return "Salcobrand"; }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try { return "https://salcobrand.cl/search_result?query=" + URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8); } 
        catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        try { 
            page.waitForLoadState();
            page.waitForTimeout(4000); // Salcobrand suele ser pesado de cargar
            
            List<String> tarjetas = page.locator(".product-card, .product, .vitrine").allInnerTexts();
            BigDecimal minPrecio = null;
            
            for (String texto : tarjetas) {
                // Atrapa el $1499 y el $999, y la lógica se quedará con el menor
                Matcher m = Pattern.compile("\\$\\s*(\\d[\\d\\.]*)").matcher(texto);
                while (m.find()) {
                    String limpio = m.group(1).replace(".", "");
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        if (actual.compareTo(new BigDecimal(150)) > 0) {
                            if (minPrecio == null || actual.compareTo(minPrecio) < 0) {
                                minPrecio = actual;
                            }
                        }
                    } catch (Exception e) {}
                }
            }
            return minPrecio;
        } catch (Exception e) { return null; }
    }

    @Override
    public boolean esBioequivalente(Page page) {
        try {
            return page.getByText(Pattern.compile("bioequivalente", Pattern.CASE_INSENSITIVE)).count() > 0 ||
                   page.locator("img[src*='bioeq' i]").count() > 0;
        } catch (Exception e) { return false; }
    }
}