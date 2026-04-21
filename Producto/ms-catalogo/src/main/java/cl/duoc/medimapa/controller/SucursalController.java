package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.dto.SucursalResponseDTO;
import cl.duoc.medimapa.service.SucursalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService servicio;

    @GetMapping
    public List<SucursalResponseDTO> listarActivas() {
        return servicio.listarSucursalesActivas();
    }
}