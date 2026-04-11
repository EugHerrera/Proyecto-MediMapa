package cl.duoc.medimapa;

import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import cl.duoc.medimapa.model.CorridaActualizacion;
import cl.duoc.medimapa.model.SucursalFarmacia;
import cl.duoc.medimapa.model.Medicamento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@SpringBootApplication
public class MedimapaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedimapaApplication.class, args);
    }

    @Bean
    public CommandLineRunner probarScraper(
            ScraperService scraperService,
            SucursalFarmaciaRepository sucursalRepo,
            MedicamentoRepository medicamentoRepo,
            CorridaActualizacionRepository corridaRepo) {
        
        return args -> {
            System.out.println("\n======================================");
            System.out.println("🛠️  PREPARANDO AMBIENTE DE BASE DE DATOS");
            System.out.println("======================================");

            // 1. BUSCAMOS O CREAMOS UNA SUCURSAL (Para evitar error de FK)
            // Intentamos buscar la sucursal con ID 1, si no existe, la creamos.
            SucursalFarmacia sucursal = sucursalRepo.findById(1L).orElseGet(() -> {
                System.out.println("📝 Creando sucursal de prueba...");
                SucursalFarmacia s = new SucursalFarmacia();
                s.setNombre_sucursal("Farmacia Ahumada Central");
                s.setDireccion("Av. Libertador Bernardo O'Higgins 123");
                s.setLatitud(new BigDecimal("-33.4489"));
                s.setLongitud(new BigDecimal("-70.6693"));
                s.setActivo(true);
                s.setCreadoEn(OffsetDateTime.now());
                return sucursalRepo.save(s);
            });

            // 2. BUSCAMOS O CREAMOS UN MEDICAMENTO CANÓNICO
            Medicamento medicamento = medicamentoRepo.findAll().stream()
                .filter(m -> m.getNombre_canonico().equalsIgnoreCase("Paracetamol 500mg"))
                .findFirst()
                .orElseGet(() -> {
                    System.out.println("📝 Creando medicamento de prueba...");
                    Medicamento m = new Medicamento();
                    m.setNombre_canonico("Paracetamol 500mg");
                    m.setPrincipio_activo("Paracetamol");
                    m.setOrigen_catalogo("MANUAL");
                    m.setActivo(true);
                    return medicamentoRepo.save(m);
                });

            // 3. CREAMOS UNA NUEVA CORRIDA (Obligatoria según tu modelo)
            System.out.println("📝 Registrando nueva corrida de actualización...");
            CorridaActualizacion corrida = new CorridaActualizacion();
            corrida.setId_fuente(1L); // ID de la fuente (ej: Ahumada)
            corrida.setInicio(OffsetDateTime.now());
            corrida.setEstado("PROCESANDO");
            corrida = corridaRepo.save(corrida);

            System.out.println("✅ Ambiente listo. Llamando al robot...");

            // 4. EJECUTAMOS EL SCRAPER PASÁNDOLE LOS OBJETOS REALES
            String urlAhumada = "https://www.farmaciasahumada.cl/catalogsearch/result/?q=paracetamol";
            String textoBusqueda = "paracetamol";

            try {
                scraperService.extraerYGuardarPrecio(urlAhumada, textoBusqueda, sucursal, medicamento, corrida);
                
                // Si todo sale bien, actualizamos el estado de la corrida
                corrida.setEstado("ok");
                corrida.setFin(OffsetDateTime.now());
                corridaRepo.save(corrida);
                
            } catch (Exception e) {
                corrida.setEstado("error");
                corrida.setDetalle_error(e.getMessage());
                corrida.setFin(OffsetDateTime.now());
                corridaRepo.save(corrida);
                System.err.println("❌ Error en la ejecución del scraper: " + e.getMessage());
            }

            System.out.println("======================================\n");
        };
    }
}