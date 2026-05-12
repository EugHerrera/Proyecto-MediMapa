package cl.duoc.medimapa;

import cl.duoc.medimapa.model.CorridaActualizacion;
import cl.duoc.medimapa.repository.CorridaActualizacionRepository;
import cl.duoc.medimapa.repository.MedicamentoRepository;
import cl.duoc.medimapa.repository.SucursalFarmaciaRepository;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.OffsetDateTime;

@SpringBootApplication
@EnableScheduling
public class MedimapaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedimapaApplication.class, args);
    }

    @Bean
    public CommandLineRunner iniciarScraperAutomático(
            ScraperService scraperService,
            SucursalFarmaciaRepository sucursalRepo,
            MedicamentoRepository medicamentoRepo,
            CorridaActualizacionRepository corridaRepo) {
        
        return args -> {
            System.out.println("\n======================================");
            System.out.println(" INICIANDO SISTEMA MEDIMAPA");
            System.out.println("======================================");

            System.out.println("Leyendo sucursales y catálogo de medicamentos desde PostgreSQL...");

            // 1. Registramos el inicio del trabajo del robot
            System.out.println(" Registrando nueva corrida de actualización masiva...");
            CorridaActualizacion corrida = new CorridaActualizacion();
            corrida.setId_fuente(0L); // 0 = Corrida general multi-fuente
            corrida.setInicio(OffsetDateTime.now());
            corrida.setEstado("parcial"); 

            try {
                corrida = corridaRepo.save(corrida);
                System.out.println("Corrida guardada. Despertando al robot para actualizar los medicamentos...");
            } catch (Exception e) {
                System.err.println("Advertencia BD: No se pudo guardar la corrida inicial. Ejecutando en memoria...");
            }

            // 2. El robot hace su trabajo con los medicamentos que ya tienes en la BD
            try {
                scraperService.ejecutarScrapingAutomatico(corrida);
                
                corrida.setEstado("ok");
                corrida.setFin(OffsetDateTime.now());
                
                try {
                    corridaRepo.save(corrida);
                } catch (Exception ignored) {} 
                
                System.out.println("Actualización de base de datos terminada con éxito");
                
            } catch (Exception e) {
                corrida.setEstado("error");
                corrida.setDetalle_error(e.getMessage());
                corrida.setFin(OffsetDateTime.now());
                
                try {
                    corridaRepo.save(corrida);
                } catch (Exception ignored) {}
                
                System.err.println("Error crítico en el scraper al iniciar: " + e.getMessage());
            }

            System.out.println("======================================\n");
        };
    }
}