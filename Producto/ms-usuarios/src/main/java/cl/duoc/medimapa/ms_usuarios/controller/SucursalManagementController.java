package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.model.SucursalFarmacia;
import cl.duoc.medimapa.ms_usuarios.repository.SucursalFarmaciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios/farmacias-admin")
public class SucursalManagementController {

    @Autowired
    private SucursalFarmaciaRepository sucursalRepo;

    // 1. LISTAR TODAS (Para el Admin)
    @GetMapping
    public List<SucursalFarmacia> listarTodas() {
        return sucursalRepo.findAll();
    }

    // 2. CREAR NUEVA SUCURSAL
    @PostMapping
    public SucursalFarmacia crear(@RequestBody SucursalFarmacia sucursal) {
        sucursal.setCreadoEn(OffsetDateTime.now());
        sucursal.setActivo(true);
        return sucursalRepo.save(sucursal);
    }

    // 3. ACTUALIZAR SUCURSAL EXISTENTE
    @PutMapping("/{id}")
    public ResponseEntity<SucursalFarmacia> actualizar(@PathVariable Long id, @RequestBody SucursalFarmacia datosNuevos) {
        return sucursalRepo.findById(id).map(sucursal -> {
            sucursal.setNombre_sucursal(datosNuevos.getNombre_sucursal());
            sucursal.setDireccion(datosNuevos.getDireccion());
            sucursal.setLatitud(datosNuevos.getLatitud());
            sucursal.setLongitud(datosNuevos.getLongitud());
            sucursal.setActivo(datosNuevos.getActivo());
            sucursal.setActualizadoEn(OffsetDateTime.now());
            return ResponseEntity.ok(sucursalRepo.save(sucursal));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. ELIMINAR (O DESACTIVAR)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (sucursalRepo.existsById(id)) {
            sucursalRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}