package cl.duoc.medimapa;

import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
            System.out.println("🛠️ PREPARANDO AMBIENTE MULTI-FARMACIA (3 CADENAS)");
            System.out.println("======================================");

            // 1. SUCURSALES (Data Seeding)
            // Ya no creamos sucursales aquí. Las sucursales físicas de La Florida
            // ya fueron cargadas directamente en PostgreSQL por el script SQL.
            System.out.println("✅ Leyendo sucursales reales de La Florida desde la Base de Datos...");

            // 2. CREAMOS UNA NUEVA CORRIDA (Protegida contra el ck_corrida_estado)
            System.out.println("📝 Registrando nueva corrida de actualización masiva...");
            CorridaActualizacion corrida = new CorridaActualizacion();
            corrida.setId_fuente(0L); // 0 indica que fue una corrida general (multi-fuente)
            corrida.setInicio(OffsetDateTime.now());
            
            // ¡ESTADO CORREGIDO AQUÍ!
            corrida.setEstado("parcial"); 

            try {
                corrida = corridaRepo.save(corrida);
                System.out.println("✅ Corrida guardada en BD con estado 'parcial'. Despertando al robot...");
            } catch (Exception e) {
                System.err.println("⚠️ Advertencia BD: No se pudo guardar la corrida por restricción de estado. Ejecutando en memoria...");
            }

            // 3. EL ROBOT INICIA SU TRABAJO AUTÓNOMO
            try {
                // Pasamos la corrida al robot para que haga su magia
                scraperService.ejecutarScrapingAutomatico(corrida);
                
                // ¡ESTADO CORREGIDO AQUÍ!
                corrida.setEstado("ok");
                corrida.setFin(OffsetDateTime.now());
                
                try {
                    corridaRepo.save(corrida);
                } catch (Exception ignored) {} // Ignoramos si la BD vuelve a rechazar el estado
                
                System.out.println("🎉 ¡Corrida de las 3 Farmacias terminada con éxito!");
                
            } catch (Exception e) {
                // ¡ESTADO CORREGIDO AQUÍ!
                corrida.setEstado("error");
                corrida.setDetalle_error(e.getMessage());
                corrida.setFin(OffsetDateTime.now());
                
                try {
                    corridaRepo.save(corrida);
                } catch (Exception ignored) {}
                
                System.err.println("❌ Error crítico en el scraper: " + e.getMessage());
            }

            System.out.println("======================================\n");
        };
    }
}