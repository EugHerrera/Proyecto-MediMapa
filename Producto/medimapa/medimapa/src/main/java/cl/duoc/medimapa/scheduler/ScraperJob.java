package cl.duoc.medimapa.scheduler;

import cl.duoc.medimapa.model.CorridaActualizacion;
import cl.duoc.medimapa.repository.CorridaActualizacionRepository;
import cl.duoc.medimapa.service.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ScraperJob {

    private static final Logger logger = LoggerFactory.getLogger(ScraperJob.class);

    @Autowired
    private ScraperService scraperService;

    @Autowired
    private CorridaActualizacionRepository corridaRepo;

    // cron = "0 0 3 * * *" -> Significa "Ejecutar todos los días a las 3:00 AM"
    @Scheduled(cron = "0 0 3 * * *")
    public void actualizarBaseDeDatosNocturna() {
        logger.info("Iniciando reloj nocturno: Despertando al Motor Scraper...");

        // 1. Creacion de la bitácora de la corrida 
        CorridaActualizacion corrida = new CorridaActualizacion();
        corrida.setId_fuente(0L); 
        corrida.setInicio(OffsetDateTime.now());
        corrida.setEstado("parcial");

        try {
            corrida = corridaRepo.save(corrida);
        } catch (Exception e) {
            logger.warn(" Advertencia: No se pudo guardar la corrida inicial. Ejecutando igual...");
        }

        // 2. Llamamos a tu método REAL. 
        // Él se encargará de buscar en PostgreSQL los medicamentos y hacer el scraping.
        try {
            scraperService.ejecutarScrapingAutomatico(corrida);

            // 3. Si todo sale bien, marcamos la corrida como exitosa
            corrida.setEstado("ok");
            corrida.setFin(OffsetDateTime.now());
            try {
                corridaRepo.save(corrida);
            } catch (Exception ignored) {}

            logger.info(" Scraper Nocturno Finalizado con éxito. ¡Caché actualizado!");

        } catch (Exception e) {
            // Si algo falla a nivel global, lo registramos
            corrida.setEstado("error");
            corrida.setDetalle_error(e.getMessage());
            corrida.setFin(OffsetDateTime.now());
            try {
                corridaRepo.save(corrida);
            } catch (Exception ignored) {}

            logger.error(" Error grave en el scraper nocturno: {}", e.getMessage());
        }
    }
}