package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.SucursalFarmacia;
import cl.duoc.medimapa.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@CrossOrigin(origins = "*") // Escudo antipánico para el Frontend
public class SucursalController {

    @Autowired
    private SucursalRepository sucursalRepo;

    // Endpoint: GET http://localhost:8081/api/sucursales/listar
    @GetMapping("/listar")
    public List<SucursalFarmacia> listarSucursales() {
        return sucursalRepo.findByActivoTrue();
    }
}