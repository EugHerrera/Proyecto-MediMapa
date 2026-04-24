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
public class SimiScraper implements FarmaciaScraper {
    @Override public Long getIdFuente() { return 2L; }
    @Override public String getNombreFarmacia() { return "Farmacias Dr. Simi"; }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            String encoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            return "https://www.drsimi.cl/" + encoded + "?_q=" + encoded + "&map=ft&order=OrderByPriceASC";
        } catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        try { 
            page.waitForLoadState();
            // 🔥 SCROLL MAGICO: Obligamos a la página a bajar para que cargue los precios
            page.evaluate("window.scrollBy(0, 800)");
            page.waitForTimeout(4500); // Le damos tiempo a que aparezcan
            
            List<String> textos = page.locator("body").allInnerTexts();
            BigDecimal minPrecio = null;
            
            for (String texto : textos) {
                // Regex para atrapar todo lo que sea $100, $1.000, etc.
                Matcher m = Pattern.compile("\\$\\s*(\\d[\\d\\.]*)").matcher(texto);
                while (m.find()) {
                    String limpio = m.group(1).replace(".", "");
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        if (actual.compareTo(new BigDecimal(150)) > 0) { // Evita basura menor a $150
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
            return page.getByText(Pattern.compile("bioequivalente", Pattern.CASE_INSENSITIVE)).count() > 0;
        } catch (Exception e) { return false; }
    }
}