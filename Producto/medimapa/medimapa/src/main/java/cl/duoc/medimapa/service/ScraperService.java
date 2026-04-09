package cl.duoc.medimapa.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ScraperService {

    public void extraerPrecioCruzVerde(String url) {
        try {
            // 1. Simular un navegador real (User-Agent) para evitar bloqueos
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            // 2. BUSCAR METADATOS (Estrategia Avanzada)
            // La mayoría de las farmacias grandes incluyen un script tipo "application/ld+json"
            Element scriptTag = doc.select("script[type=application/ld+json]").first();
            
            if (scriptTag != null) {
                String jsonContent = scriptTag.html();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonContent);

                // 3. EXTRAER DATOS DEL JSON
                // Estos campos son estándar en el comercio electrónico profesional
                String nombre = root.path("name").asText();
                String precio = root.path("offers").path("price").asText();
                String moneda = root.path("offers").path("priceCurrency").asText();

                System.out.println("--- DATOS EXTRAÍDOS CON ÉXITO ---");
                System.out.println("Producto: " + nombre);
                System.out.println("Precio Actual: " + moneda + " " + precio);
                System.out.println("---------------------------------");
                
                // Aquí conectarías con tu entidad PrecioVigente y guardarías en la BD
            } else {
                System.out.println("No se encontraron metadatos estructurados. Intentando por Selectores CSS...");
                // Fallback: Si no hay JSON, buscamos el precio por clase (más frágil)
                String precioAlternativo = doc.select(".price-sales").text(); 
                System.out.println("Precio por selector: " + precioAlternativo);
            }

        } catch (Exception e) {
            System.err.println("Error técnico al scrapear Cruz Verde: " + e.getMessage());
        }
    }
}