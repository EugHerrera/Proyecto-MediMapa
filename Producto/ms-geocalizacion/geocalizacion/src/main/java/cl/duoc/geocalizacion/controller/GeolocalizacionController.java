package cl.duoc.geocalizacion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.service.GeolocalizacionService;
import java.util.List;


@RestController
@RequestMapping("/api/v1/geolocalizacion")
public class GeolocalizacionController {
    
    private final GeolocalizacionService service;

    public GeolocalizacionController(GeolocalizacionService service) {
        this.service = service;
    }

    @GetMapping("/sucursales")
    public ResponseEntity<List<SucursalFarmacia>> buscar(
            @RequestParam double lat, 
            @RequestParam double lon, 
            @RequestParam(defaultValue = "1000") double radio) {
        // La ubicación del usuario se procesa y se olvida (Ley 21.719)
        return ResponseEntity.ok(service.obtenerCercanas(lat, lon, radio));
    }
}
