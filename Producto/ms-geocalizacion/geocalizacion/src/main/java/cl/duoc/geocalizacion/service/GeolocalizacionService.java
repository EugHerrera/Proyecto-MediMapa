package cl.duoc.geocalizacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.repository.SucursalFarmaciaRepository;

@Service
public class GeolocalizacionService {

    @Autowired
    private SucursalFarmaciaRepository sucursalRepository;

    /**
     * Busca sucursales cercanas a una ubicación específica
     * @param lat Latitud del usuario
     * @param lon Longitud del usuario
     * @param radio Radio de búsqueda en metros
     * @return Lista de sucursales cercanas
     */
    public List<SucursalFarmacia> obtenerCercanas(double lat, double lon, double radio) {
        // La ubicación del usuario se procesa y se olvida (Ley 21.719)
        return sucursalRepository.buscarCercanas(lat, lon, radio);
    }
}

