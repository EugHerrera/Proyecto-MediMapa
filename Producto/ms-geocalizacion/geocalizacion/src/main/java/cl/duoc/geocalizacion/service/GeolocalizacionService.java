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

    public List<SucursalFarmacia> obtenerCercanas(Point ubicacion, double radio) {
        double lat = ubicacion.getY(); 
        double lng = ubicacion.getX(); 
        
        return sucursalRepository.buscarCercanas(lat, lng, radio);
    }
}