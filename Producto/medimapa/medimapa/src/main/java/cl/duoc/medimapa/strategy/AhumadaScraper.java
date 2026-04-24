
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
public class AhumadaScraper implements FarmaciaScraper {
    @Override public Long getIdFuente() { return 1L; }
    @Override public String getNombreFarmacia() { return "Farmacias Ahumada"; }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try { return "https://www.farmaciasahumada.cl/search?q=" + URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8); } 
        catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        try { 
            page.waitForLoadState();
            page.waitForTimeout(4000); 
            
            List<String> textos = page.locator(".price, .price-wrapper, [class*='Price']").allInnerTexts();
            BigDecimal minPrecio = null;
            Pattern pattern = Pattern.compile("\\$\\s*(\\d[\\d\\.]*)");
            
            for (String texto : textos) {
                Matcher m = pattern.matcher(texto);
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
            // 1. Buscamos la palabra literal en cualquier texto visible
            boolean porTexto = page.getByText(Pattern.compile("bioequivalente", Pattern.CASE_INSENSITIVE)).count() > 0;
            
            // 2. Red ampliada: Buscamos en imágenes, textos alternativos (alt) y gráficos SVG
            boolean porAtributo = page.locator(
                "img[src*='bio' i], " +           // Imágenes que tengan 'bio' en la ruta
                "img[alt*='bio' i], " +           // Imágenes con texto oculto 'bio'
                "[class*='bioeq' i], " +          // Etiquetas con clases CSS de bioequivalencia
                "svg[aria-label*='bio' i], " +    // Gráficos modernos con etiquetas ocultas
                "svg[class*='bio' i]"             // Gráficos modernos con clases CSS
            ).count() > 0;

            return porTexto || porAtributo;
        } catch (Exception e) { return false; }
    }
}