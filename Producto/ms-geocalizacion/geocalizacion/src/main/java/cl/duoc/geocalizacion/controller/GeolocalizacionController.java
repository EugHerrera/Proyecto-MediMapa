package cl.duoc.geocalizacion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.service.GeolocalizacionService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/geolocalizacion")
@CrossOrigin(origins = "")
public class GeolocalizacionController {
private final GeolocalizacionService service;

    public GeolocalizacionController(GeolocalizacionService service) {
        this.service = service;
    }

    @GetMapping("/sucursales")
    public ResponseEntity<List<SucursalFarmacia>> getSucursales(
            @RequestParam double lat, 
            @RequestParam double lon, 
            @RequestParam(defaultValue = "1000") double radio) {
        
        // procesamiento volátil del radio a metros
        return ResponseEntity.ok(service.obtenerCercanas(lat, lon, radio));
    }
}
