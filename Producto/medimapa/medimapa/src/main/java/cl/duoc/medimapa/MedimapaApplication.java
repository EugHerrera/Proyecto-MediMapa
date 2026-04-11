package cl.duoc.medimapa;

import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
            System.out.println("🛠️ PREPARANDO AMBIENTE MULTI-FARMACIA (3 CADENAS)");
            System.out.println("======================================");

            // 1. PREPARAMOS LAS SUCURSALES (Ahumada ID 1, Simi ID 2, Salcobrand ID 3)
            sucursalRepo.findById(1L).orElseGet(() -> {
                System.out.println("📝 Creando sucursal: Farmacia Ahumada...");
                SucursalFarmacia s1 = new SucursalFarmacia();
                s1.setNombre_sucursal("Farmacia Ahumada Central");
                s1.setDireccion("Av. Libertador Bernardo O'Higgins 123");
                s1.setLatitud(new BigDecimal("-33.4489"));
                s1.setLongitud(new BigDecimal("-70.6693"));
                s1.setActivo(true);
                s1.setCreadoEn(OffsetDateTime.now());
                return sucursalRepo.save(s1);
            });

            sucursalRepo.findById(2L).orElseGet(() -> {
                System.out.println("📝 Creando sucursal: Farmacias Dr. Simi...");
                SucursalFarmacia s2 = new SucursalFarmacia();
                s2.setNombre_sucursal("Dr. Simi La Florida");
                s2.setDireccion("Vicuña Mackenna 7000");
                s2.setLatitud(new BigDecimal("-33.5218"));
                s2.setLongitud(new BigDecimal("-70.5985"));
                s2.setActivo(true);
                s2.setCreadoEn(OffsetDateTime.now());
                return sucursalRepo.save(s2);
            });

            sucursalRepo.findById(3L).orElseGet(() -> {
                System.out.println("📝 Creando sucursal: Farmacias Salcobrand...");
                SucursalFarmacia s3 = new SucursalFarmacia();
                s3.setNombre_sucursal("Salcobrand Centro");
                s3.setDireccion("Paseo Ahumada 200");
                s3.setLatitud(new BigDecimal("-33.4411"));
                s3.setLongitud(new BigDecimal("-70.6503"));
                s3.setActivo(true);
                s3.setCreadoEn(OffsetDateTime.now());
                return sucursalRepo.save(s3);
            });

            // 2. CREAMOS UNA NUEVA CORRIDA
            System.out.println("📝 Registrando nueva corrida de actualización masiva...");
            CorridaActualizacion corrida = new CorridaActualizacion();
            corrida.setId_fuente(0L); // 0 indica que fue una corrida general (multi-fuente)
            corrida.setInicio(OffsetDateTime.now());
            corrida.setEstado("PROCESANDO");
            corrida = corridaRepo.save(corrida);

            System.out.println("✅ Ambiente listo. Despertando al robot...");

            // 3. EL ROBOT INICIA SU TRABAJO AUTÓNOMO
            try {
                // Pasamos la corrida al robot para que haga su magia
                scraperService.ejecutarScrapingAutomatico(corrida);
                
                corrida.setEstado("ok");
                corrida.setFin(OffsetDateTime.now());
                corridaRepo.save(corrida);
                System.out.println("🎉 ¡Corrida de las 3 Farmacias terminada con éxito!");
                
            } catch (Exception e) {
                corrida.setEstado("error");
                corrida.setDetalle_error(e.getMessage());
                corrida.setFin(OffsetDateTime.now());
                corridaRepo.save(corrida);
                System.err.println("❌ Error crítico: " + e.getMessage());
            }

            System.out.println("======================================\n");
        };
    }
}