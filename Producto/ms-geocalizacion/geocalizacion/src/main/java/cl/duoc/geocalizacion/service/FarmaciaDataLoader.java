package cl.duoc.geocalizacion.service;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.repository.SucursalFarmaciaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class FarmaciaDataLoader implements CommandLineRunner {
private final SucursalFarmaciaRepository repository;

    public FarmaciaDataLoader(SucursalFarmaciaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Verificamos si ya hay datos para no duplicar
        if (repository.count() > 1) {
            System.out.println("✅ Los datos de farmacias ya están cargados en la base de datos.");
            return;
        }

        System.out.println("🚀 Servidores externos caídos. Iniciando carga automática de respaldo local...");

        // 2. Creamos una lista de farmacias reales de La Florida
        List<SucursalFarmacia> farmaciasLaFlorida = new ArrayList<>();

        farmaciasLaFlorida.add(crearFarmacia("Farmacia Ahumada - Plaza Vespucio", "Vicuña Mackenna 7110", "-33.5205", "-70.5975"));
        farmaciasLaFlorida.add(crearFarmacia("Cruz Verde - Metro Bellavista", "Vicuña Mackenna 7200", "-33.5225", "-70.5985"));
        farmaciasLaFlorida.add(crearFarmacia("Salcobrand - Paradero 14", "Froilán Roa 7100", "-33.5180", "-70.5950"));
        farmaciasLaFlorida.add(crearFarmacia("Doctor Simi - Walker Martínez", "Av. Walker Martínez 150", "-33.5150", "-70.5950"));
        farmaciasLaFlorida.add(crearFarmacia("Farmacia Ahumada - Trinidad", "Vicuña Mackenna 9100", "-33.5380", "-70.5760"));

        // 3. ¡Magia! Spring Boot guarda toda la lista de golpe en tu PostgreSQL
        repository.saveAll(farmaciasLaFlorida);
        
        System.out.println("🎉 ¡Éxito! Se inyectaron automáticamente " + farmaciasLaFlorida.size() + " farmacias en PostGIS.");
    }

    // Método auxiliar para no escribir tanto código repetido arriba
    private SucursalFarmacia crearFarmacia(String nombre, String direccion, String lat, String lon) {
        SucursalFarmacia f = new SucursalFarmacia();
        f.setNombre_sucursal(nombre);
        f.setDireccion(direccion);
        f.setLatitud(new BigDecimal(lat));
        f.setLongitud(new BigDecimal(lon));
        f.setActivo(true);
        return f;
    }
}
