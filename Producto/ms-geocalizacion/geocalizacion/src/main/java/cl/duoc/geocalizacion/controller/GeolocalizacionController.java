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

    @GetMapping("/la-florida")
    public ResponseEntity<List<SucursalFarmacia>> getLaFlorida() {
        return ResponseEntity.ok(service.obtenerFarmaciasLaFlorida());
    }
}
