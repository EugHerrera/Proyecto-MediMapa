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

            // 1. PREPARAMOS LAS SUCURSALES (Con protección try-catch por si falta id_comuna)
            try {
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
            } catch (Exception e) {
                System.err.println("⚠️ Advertencia BD: No se pudieron crear las sucursales de prueba. (Posible restricción id_comuna)");
            }

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