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

    public List<SucursalFarmacia> obtenerCercanas(double lat, double lon, double radio) {
        // Se llama a la consulta espacial nativa
        return repository.buscarCercanas(lat, lon, radio);
    }
}

