package cl.duoc.geocalizacion.service;

import org.springframework.stereotype.Service;
import java.util.List;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.repository.SucursalFarmaciaRepository;

@Service
public class GeolocalizacionService {
private final SucursalFarmaciaRepository repository;

    // Inyección por constructor (Práctica recomendada para escalabilidad)
    public GeolocalizacionService(SucursalFarmaciaRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene farmacias dentro de un rango específico de coordenadas.
     * Este método es genérico, lo que permite reutilizarlo para cualquier comuna.
     */
    public List<SucursalFarmacia> obtenerFarmaciasPorRango(double minLat, double maxLat, double minLon, double maxLon) {
        return repository.buscarEnRangoCoordenadas(minLat, maxLat, minLon, maxLon);
    }

    /**
     * Lógica específica para La Florida.
     * Centralizamos las coordenadas aquí para que el Controller sea más corto.
     */
    public List<SucursalFarmacia> obtenerFarmaciasLaFlorida() {
        double minLat = -33.6000;
        double maxLat = -33.4800;
        double minLon = -70.6300;
        double maxLon = -70.5300;
        
        return repository.buscarEnRangoCoordenadas(minLat, maxLat, minLon, maxLon);
    }
}
