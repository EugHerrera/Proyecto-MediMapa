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
        try { 
            // 🔥 MAGIA: Junta el número con el "mg" automáticamente
            String queryAjustado = nombreMedicamento.trim().replaceAll("(\\d)\\s+([a-zA-Z])", "$1$2");
            // Salcobrand usa el signo '+' para separar las palabras, no lo cambiamos
            return "https://salcobrand.cl/search_result?query=" + URLEncoder.encode(queryAjustado, StandardCharsets.UTF_8); 
        } 
        catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page, String nombreMedicamento) {
        try { 
            page.waitForLoadState();
            page.waitForTimeout(4000); 
            
            List<String> tarjetas = page.locator(".product-card, .product, .vitrine, article").allInnerTexts();
            System.out.println("👉 Salcobrand encontró " + tarjetas.size() + " tarjetas de producto.");
            
            BigDecimal minPrecio = null;
            Pattern pattern = Pattern.compile("\\$\\s*(\\d[\\d\\.]*)");
            
            for (String textoTarjeta : tarjetas) {
                if (!esCoincidenciaValida(textoTarjeta, nombreMedicamento)) continue;

                Matcher m = pattern.matcher(textoTarjeta);
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