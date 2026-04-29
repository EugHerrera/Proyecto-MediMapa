package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import cl.duoc.medimapa.ms_usuarios.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/medicamentos-admin")
public class MedicamentoManagementController {

    @Autowired
    private MedicamentoRepository medicamentoRepo;

    // 1. LISTAR TODO EL CATÁLOGO
    @GetMapping
    public List<Medicamento> listarTodo() {
        return medicamentoRepo.findAll();
    }

    // 2. ACTUALIZAR UN MEDICAMENTO (Nombre, Principio Activo, Bioequivalencia)
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> actualizar(@PathVariable Long id, @RequestBody Medicamento nuevosDatos) {
        return medicamentoRepo.findById(id).map(med -> {
            med.setNombre_canonico(nuevosDatos.getNombre_canonico());
            med.setPrincipio_activo(nuevosDatos.getPrincipio_activo());
            med.setEs_bioequivalente(nuevosDatos.getEs_bioequivalente());
            med.setActivo(nuevosDatos.getActivo());
            return ResponseEntity.ok(medicamentoRepo.save(med));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. ELIMINAR DEL CATÁLOGO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (medicamentoRepo.existsById(id)) {
            medicamentoRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}