package cl.duoc.medimapa.strategy;

import com.microsoft.playwright.Page;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface FarmaciaScraper {
    Long getIdFuente(); 
    String getNombreFarmacia(); 
    String generarUrl(String nombreMedicamento);  
    BigDecimal extraerMenorPrecio(Page page, String nombreMedicamento); 
    boolean esBioequivalente(Page page);

    default boolean esCoincidenciaValida(String textoTarjeta, String busqueda) {
        if (textoTarjeta == null || busqueda == null) return false;
        
        String tarjetaLower = textoTarjeta.toLowerCase();
        String busquedaLower = busqueda.toLowerCase();


        Matcher m = Pattern.compile("\\d+[.,]?\\d*").matcher(busquedaLower);

        while (m.find()) {
            String numeroDosis = m.group();

            if (!tarjetaLower.contains(numeroDosis)) {
                return false;
            }
        }
        return true;
    }
}