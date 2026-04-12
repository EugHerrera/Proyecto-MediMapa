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

    @Override
    public Long getIdFuente() {
        return 2L; // ID para Dr. Simi en tu base de datos
    }

    @Override
    public String getNombreFarmacia() {
        return "Farmacias Dr. Simi";
    }

    @Override
    public String generarUrl(String nombreMedicamento) {
        try {
            // Dr. Simi suele usar una estructura de URL directa para búsquedas
            String busquedaEncoded = URLEncoder.encode(nombreMedicamento, StandardCharsets.UTF_8);
            return "https://www.drsimi.cl/" + busquedaEncoded + "?_q=" + busquedaEncoded + "&map=ft";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public BigDecimal extraerMenorPrecio(Page page) {
        // Aplicamos el "Modo Dios": escaneamos todo lo que tenga un signo "$"
        // Dr. Simi usa mucho contenido dinámico, así que buscamos en spans y divs
        Locator precios = page.locator("span, div").filter(new Locator.FilterOptions().setHasText("$"));
        
        if (precios.count() > 0) {
            List<String> textos = precios.allInnerTexts();
            BigDecimal minPrecio = null;

            for (String t : textos) {
                // Limpieza profunda para quedarnos solo con los dígitos
                String limpio = t.replaceAll("[^\\d]", "");
                if (!limpio.isEmpty()) {
                    try {
                        BigDecimal actual = new BigDecimal(limpio);
                        
                        // Filtro de seguridad: ignoramos valores irreales o centavos
                        if (actual.compareTo(new BigDecimal(100)) > 0) {
                            if (minPrecio == null || actual.compareTo(minPrecio) < 0) {
                                minPrecio = actual;
                            }
                        }
                    } catch (Exception e) {
                        // Si el texto no era un número puro, lo saltamos
                    }
                }
            }
            return minPrecio;
        }
        return null;
    }
}