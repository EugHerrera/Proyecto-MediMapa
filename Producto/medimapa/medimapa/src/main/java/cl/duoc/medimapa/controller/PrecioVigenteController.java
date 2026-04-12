package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
// IMPORTANTE: Agregamos estas dos importaciones nuevas
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scraper")
// ⬇️ ESTA ES LA LÍNEA CLAVE QUE DEBES AGREGAR ⬇️
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class PrecioVigenteController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    @GetMapping("/precios")
    public List<PrecioVigente> obtenerTodosLosPrecios() {
        System.out.println("📡 Enviando precios al Frontend...");
        return precioRepo.findAll();
    }
}