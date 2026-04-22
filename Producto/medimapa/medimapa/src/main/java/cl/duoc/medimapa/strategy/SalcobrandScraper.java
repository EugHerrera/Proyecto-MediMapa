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
    @Override public Long getIdFuente() { return 3L; }
    @Override public String getNombreFarmacia() { return "Salcobrand"; }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            return "https://salcobrand.cl/search_result?query=" + URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
        } catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        try { page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(8000)); } catch (Exception e) { page.waitForTimeout(3000); }
        Locator precios = page.locator("[class*='price'], [class*='Precio'], .bestPrice").filter(new Locator.FilterOptions().setHasText("$"));
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;
            for (String t : textos) {
                String limpio = t.split("[\\n\\s]+")[0].replaceAll("[^\\d]", "");
                if (!limpio.isEmpty()) {
                    try {
                        int valor = Integer.parseInt(limpio);
                        if (valor > 550 && valor != 5000 && valor != 10000 && valor != 15000 && valor != 20000 && valor != 25000) {
                            BigDecimal actual = new BigDecimal(limpio);
                            if (minPrecio == null || actual.compareTo(minPrecio) < 0) minPrecio = actual;
                        }
                    } catch (Exception e) {}
                }
            }
            return minPrecio;
        }
        return null;
    }

    // 🔥 EL NUEVO RADAR BIOEQUIVALENTE
    @Override
    public boolean esBioequivalente(Page page) {
        try {
            return page.locator("img[src*='bioequivalente' i], img[alt*='bioequivalente' i], [class*='bioequivalente' i]").count() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}