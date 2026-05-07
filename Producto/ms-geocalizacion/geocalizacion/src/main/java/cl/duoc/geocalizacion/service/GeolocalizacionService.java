package cl.duoc.geocalizacion.service;

import org.locationtech.jts.geom.Point;
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
     * @param ubicacion Ubicación del usuario como Point
     * @param radio Radio de búsqueda en metros
     * @return Lista de sucursales cercanas
     */
    public List<SucursalFarmacia> obtenerCercanas(Point ubicacion, double radio) {
        // La ubicación del usuario se procesa y se olvida (Ley 21.719)
        
        // Extraemos las coordenadas del Point (Y = Latitud, X = Longitud)
        double lat = ubicacion.getY();
        double lng = ubicacion.getX();
        
        // Le pasamos los números pelados a la consulta nativa
        return sucursalRepository.buscarCercanas(lat, lng, radio);
    }
}