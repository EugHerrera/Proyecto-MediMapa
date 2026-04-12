package cl.duoc.medimapa.scheduler;

import cl.duoc.medimapa.model.*;
import cl.duoc.medimapa.repository.*;
import cl.duoc.medimapa.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ScraperScheduler {

    @Autowired
    private ScraperService scraperService;

    @Autowired
    private CorridaActualizacionRepository corridaRepo;

    // Se ejecuta cada 5 minutos para probar (300000 milisegundos)
    // Cuando quieras que sea diario a las 3AM, usas @Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(fixedDelay = 300000)
    public void tareaProgramadaScraping() {
        System.out.println("⏰ [CRON] Iniciando tarea programada Multi-Farmacia...");

        // 1. Creamos la corrida (ID fuente 0 = Multi-farmacia)
        CorridaActualizacion corrida = new CorridaActualizacion();
        corrida.setId_fuente(0L); 
        corrida.setInicio(OffsetDateTime.now());
        corrida.setEstado("PROCESANDO_CRON");
        corrida = corridaRepo.save(corrida);

        try {
            // 2. Ejecutamos pasándole SOLO la corrida. 
            // ¡El ScraperService buscará las sucursales por sí solo!
            scraperService.ejecutarScrapingAutomatico(corrida);

            corrida.setEstado("ok");
            corrida.setFin(OffsetDateTime.now());
            corridaRepo.save(corrida);
            System.out.println("⏰ [CRON] Tarea finalizada con éxito.");
            
        } catch (Exception e) {
            corrida.setEstado("error");
            corrida.setDetalle_error(e.getMessage());
            corrida.setFin(OffsetDateTime.now());
            corridaRepo.save(corrida);
            System.err.println("⏰ [CRON] Error en la tarea: " + e.getMessage());
        }
    }
}