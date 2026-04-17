package cl.duoc.medimapa.controller;

import cl.duoc.medimapa.model.MedicamentoCatalogo;
import cl.duoc.medimapa.model.PrecioVigente;
import cl.duoc.medimapa.repository.MedicamentoCatalogoRepository;
import cl.duoc.medimapa.repository.PrecioVigenteRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {
@Autowired
    private PrecioVigenteRepository precioRepo;

    @Autowired
    private MedicamentoCatalogoRepository catalogoRepo;
    @GetMapping("/precios")
    @Transactional(readOnly = true) // Esto evita que la conexión a la base de datos se cierre antes de tiempo
    public ResponseEntity<?> buscarMedicamentoCompleto(@RequestParam String nombre) {
        try {
            String principioActivo;
            Optional<MedicamentoCatalogo> traduccion = catalogoRepo.findByNombreComercialIgnoreCase(nombre);
            
            if (traduccion.isPresent()) {
                principioActivo = traduccion.get().getPrincipioActivo();
            } else {
                principioActivo = nombre;
            }
            
            // 1. Buscamos en la base de datos
            List<PrecioVigente> resultadosBD = precioRepo.buscarBioequivalentesConPrecio(principioActivo);
            
            if (resultadosBD != null && !resultadosBD.isEmpty()) {
                
                // 2. EL MAPEO MANUAL
                List<Map<String, Object>> resultadosLimpios = new ArrayList<>();
                
                for (PrecioVigente pv : resultadosBD) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("precio_max_vta", pv.getPrecioMaxVta());
                    item.put("moneda", pv.getMoneda());
                    
                    Map<String, Object> med = new HashMap<>();
                    if (pv.getMedicamento() != null) {
                        med.put("nombreCanonico", pv.getMedicamento().getNombreCanonico());
                    } else {
                        med.put("nombreCanonico", "Medicamento sin nombre");
                    }
                    item.put("medicamento", med);
                    
                    Map<String, Object> suc = new HashMap<>();
                    if (pv.getSucursal() != null) {
                        suc.put("nombreSucursal", pv.getSucursal().getNombre_sucursal());
                        suc.put("direccion", pv.getSucursal().getDireccion());
                    } else {
                        suc.put("nombreSucursal", "Farmacia Desconocida");
                        suc.put("direccion", "Sin dirección");
                    }
                    item.put("sucursal", suc);
                    
                    resultadosLimpios.add(item);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("termino_buscado", nombre);
                response.put("principio_activo", principioActivo);
                response.put("total_encontrados", resultadosLimpios.size());
                response.put("resultados", resultadosLimpios);
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            // ¡EL DETECTOR DE ERRORES!
            // Imprimimos el error en la consola de VS Code...
            e.printStackTrace(); 
            
            // ...Y lo mandamos a tu navegador para que lo leas de inmediato.
            String mensajeError = "💥 ERROR INTERNO EN JAVA: " + e.getMessage();
            if (e.getCause() != null) {
                mensajeError += " | CAUSA RAÍZ: " + e.getCause().getMessage();
            }
            return ResponseEntity.status(500).body(mensajeError);
        }
    }
}