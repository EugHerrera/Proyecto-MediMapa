package cl.duoc.geocalizacion.service;

import org.springframework.stereotype.Service;
import java.util.List;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.repository.SucursalFarmaciaRepository;

@Service
public class GeolocalizacionService {


private final SucursalFarmaciaRepository repository;

    public GeolocalizacionService(SucursalFarmaciaRepository repository) {
        this.repository = repository;
    }

    public List<SucursalFarmacia> obtenerCercanas(double lat, double lon, double radioEnMetros) {
        // Convertimos los metros a kilómetros para la fórmula (1000m -> 1km)
        double radioKm = radioEnMetros / 1000.0;
        return repository.buscarCercanas(lat, lon, radioKm);
    }
}

