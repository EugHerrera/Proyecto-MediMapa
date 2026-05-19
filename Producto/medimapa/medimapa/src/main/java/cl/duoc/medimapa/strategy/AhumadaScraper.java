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
        try { 
            String queryLimpio = nombreMedicamento.trim();
            String encodedQuery = URLEncoder.encode(queryLimpio, StandardCharsets.UTF_8);
            return "https://www.farmaciasahumada.cl/search?q=" + encodedQuery + "&search-button=&lang=null"; 
        } 
        catch (Exception e) { return ""; }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page, String nombreMedicamento) {
        try { 
            page.waitForLoadState();
            page.evaluate("window.scrollBy(0, 1000)");
            page.waitForTimeout(5000); 
            
            String selectoresMasivos = "article, div[class*='product'], div[class*='Product'], div[class*='card'], div[class*='Card'], div[class*='item'], div[class*='Item']";
            List<String> tarjetas = page.locator(selectoresMasivos).allInnerTexts();
            
            BigDecimal minPrecio = null;
            Pattern pattern = Pattern.compile("\\$\\s*(\\d[\\d\\.]*)");
            
            String palabraClave = nombreMedicamento.split(" ")[0].toLowerCase();
            
            for (String textoTarjeta : tarjetas) {
                String textoLimpio = textoTarjeta.replace("\n", " ").toLowerCase();
                
                if (!textoLimpio.contains(palabraClave) || !textoLimpio.contains("$")) {
                    continue;
                }

                if (!esCoincidenciaValida(textoLimpio, nombreMedicamento)) {
                    continue;
                }

                String textoSinUnitario = textoTarjeta.replaceAll("(?i)\\$\\s*[\\d\\.]+\\s*x\\s*[a-z\\.]+", "");


                Matcher m = pattern.matcher(textoSinUnitario);
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
        } catch (Exception e) { 
            System.err.println(" Error interno en Ahumada: " + e.getMessage());
            return null; 
        } 
    }

    @Override
    public boolean esBioequivalente(Page page) {
        try {
            boolean porTexto = page.getByText(Pattern.compile("bioequivalente", Pattern.CASE_INSENSITIVE)).count() > 0;
            boolean porAtributo = page.locator(
                "img[src*='bio' i], img[alt*='bio' i], [class*='bioeq' i], svg[aria-label*='bio' i], svg[class*='bio' i]"             
            ).count() > 0;
            return porTexto || porAtributo;
        } catch (Exception e) { return false; }
    }
}