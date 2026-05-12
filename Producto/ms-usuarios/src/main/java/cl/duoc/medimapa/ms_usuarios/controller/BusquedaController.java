package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import cl.duoc.medimapa.ms_usuarios.repository.PrecioVigenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/buscador")
public class BusquedaController {

    @Autowired
    private PrecioVigenteRepository precioRepo;

    @GetMapping("/medicamentos")
    public ResponseEntity<List<Map<String, Object>>> buscarMedicamentos(@RequestParam("q") String query) {
        
        List<PrecioVigente> resultados = precioRepo.buscarPorTexto(query);

        // Transformamos los datos complejos de la BD a algo simple para React
        List<Map<String, Object>> respuestaLista = resultados.stream().map(precio -> {
            Map<String, Object> mapa = new HashMap<>();
            
            mapa.put("medicamento", precio.getMedicamento().getNombre_canonico());
            
            mapa.put("precio", precio.getPrecio_max_vta());
            mapa.put("sucursal", precio.getSucursal() != null ? precio.getSucursal().getNombre_sucursal() : "Farmacia Independiente");
            mapa.put("fechaActualizacion", precio.getVigente_desde());
            return mapa;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(respuestaLista);
    }
}