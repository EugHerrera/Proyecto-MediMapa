package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scraper")
public class PrecioVigenteController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    // Sebastián y Misael llamarán a esta ruta para ver los precios que raspó tu bot
    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        return precioRepo.findAll();
    }
}