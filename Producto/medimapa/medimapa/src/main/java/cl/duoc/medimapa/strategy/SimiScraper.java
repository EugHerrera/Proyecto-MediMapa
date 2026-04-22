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
    @Override public Long getIdFuente() { return 2L; }
    @Override public String getNombreFarmacia() { return "Farmacias Dr. Simi"; }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            String busquedaEncoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            return "https://www.drsimi.cl/" + busquedaEncoded + "?_q=" + busquedaEncoded + "&map=ft&order=OrderByPriceASC";
        } catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        try { page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(8000)); } catch (Exception e) { page.waitForTimeout(3000); }
        Locator precios = page.locator("[class*='price'], [class*='Precio']").filter(new Locator.FilterOptions().setHasText("$"));
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;
            for (String t : textos) {
                String limpio = t.split("[\\n\\s]+")[0].replaceAll("[^\\d]", "");
                if (!limpio.isEmpty()) {
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        if (actual.compareTo(new BigDecimal(100)) > 0) {
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