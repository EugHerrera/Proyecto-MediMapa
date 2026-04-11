package cl.duoc.medimapa.strategy;

import com.microsoft.playwright.Page;
import java.math.BigDecimal;

public interface FarmaciaScraper {
    Long getIdFuente(); // El ID de la farmacia en tu Base de Datos
    String getNombreFarmacia(); // Para que los logs se vean bonitos
    String generarUrl(String nombreMedicamento); // Cada página tiene su propia forma de buscar
    BigDecimal extraerMenorPrecio(Page page); // El Modo Dios específico de cada HTML
}