package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.security.JwtUtil;
import cl.duoc.medimapa.ms_usuarios.model.Usuario;
import cl.duoc.medimapa.ms_usuarios.repository.UsuarioRepository;
import cl.duoc.medimapa.ms_usuarios.service.ExcelService;
import cl.duoc.medimapa.ms_usuarios.service.IspExcelService;
import cl.duoc.medimapa.ms_usuarios.repository.PrecioVigenteRepository;
import cl.duoc.medimapa.ms_usuarios.repository.SucursalFarmaciaRepository;
import cl.duoc.medimapa.ms_usuarios.repository.MedicamentoRepository;
import cl.duoc.medimapa.ms_usuarios.repository.SolicitudInscripcionRepository;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import cl.duoc.medimapa.ms_usuarios.model.PrecioVigenteId;
import cl.duoc.medimapa.ms_usuarios.model.SolicitudInscripcion; 
import cl.duoc.medimapa.ms_usuarios.model.SucursalFarmacia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired private PrecioVigenteRepository precioVigenteRepo;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private SucursalFarmaciaRepository sucursalFarmaciaRepository;
    @Autowired private MedicamentoRepository medicamentoRepository;
    @Autowired private SolicitudInscripcionRepository solicitudRepo;
    @Autowired private PasswordEncoder passwordEncoder; 
    @Autowired private JwtUtil jwtUtil; 
    @Autowired private ExcelService excelService; 
    
    // 🔥 AQUÍ INYECTAMOS TU NUEVO SERVICIO PARA EL ISP
    @Autowired private IspExcelService ispExcelService; 
    
    @Autowired private cl.duoc.medimapa.ms_usuarios.repository.CorridaActualizacionRepository corridaRepo;

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        String hash = passwordEncoder.encode(nuevoUsuario.getPasswordHash());
        nuevoUsuario.setPasswordHash(hash);
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok("Usuario registrado con éxito. Contraseña cifrada.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());
        Map<String, String> response = new HashMap<>();

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
                String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());
                response.put("mensaje", "Login exitoso");
                response.put("rol", usuario.getRol());
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        }
        response.put("error", "Correo o contraseña incorrectos");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/inventario/subir")
    public ResponseEntity<String> subirInventario(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "idSucursal", defaultValue = "99") Long idSucursal 
    ) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío.");
        }
        try {
            excelService.procesarExcelInventario(archivo, idSucursal);
            return ResponseEntity.ok("✅ Archivo '" + archivo.getOriginalFilename() + "' procesado y guardado en la Base de Datos.");
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al procesar el Excel: " + e.getMessage());
        }
    }

    @GetMapping("/inventario/listar/{idSucursal}")
    public ResponseEntity<List<Map<String, Object>>> obtenerInventarioReal(@PathVariable Long idSucursal) {
        List<PrecioVigente> inventario = precioVigenteRepo.buscarPorSucursal(idSucursal);
        
        List<Map<String, Object>> respuesta = inventario.stream().map(precio -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", precio.getMedicamento().getId_medicamento()); 
            map.put("nombre", precio.getMedicamento().getNombre_canonico());
            map.put("precio", precio.getPrecio_max_vta());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/inventario/actualizar-precio")
    public ResponseEntity<String> actualizarPrecioManual(
            @RequestParam("idSucursal") Long idSucursal,
            @RequestParam("textoBusqueda") String textoBusqueda,
            @RequestParam("nuevoPrecio") Double nuevoPrecio) {
        
        return precioVigenteRepo.buscarPorSucursal(idSucursal).stream()
                .filter(p -> p.getId().getTexto_busqueda().equalsIgnoreCase(textoBusqueda))
                .findFirst()
                .map(precio -> {
                    precio.setPrecio_max_vta(java.math.BigDecimal.valueOf(nuevoPrecio));
                    precio.setVigente_desde(java.time.OffsetDateTime.now());
                    precioVigenteRepo.save(precio);
                    return ResponseEntity.ok("✅ Precio actualizado correctamente a $" + nuevoPrecio);
                })
                .orElse(ResponseEntity.badRequest().body("❌ No se encontró el medicamento en esta sucursal."));
    }

    @PostMapping("/solicitud-inscripcion")
    public ResponseEntity<String> recibirSolicitud(@RequestBody SolicitudInscripcion nuevaSolicitud) {
        try {
            nuevaSolicitud.setEstado_solicitud("PENDIENTE");
            nuevaSolicitud.setFecha_solicitud(java.time.OffsetDateTime.now());
            if (nuevaSolicitud.getAcepta_ley_21719() == null || !nuevaSolicitud.getAcepta_ley_21719()) {
                return ResponseEntity.badRequest().body("❌ Error: Debe aceptar la Ley 21.719 para procesar los datos.");
            }
            solicitudRepo.save(nuevaSolicitud);
            return ResponseEntity.ok("✅ Solicitud enviada con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al guardar la solicitud: " + e.getMessage());
        }
    }

    @GetMapping("/solicitudes/pendientes")
    public ResponseEntity<List<SolicitudInscripcion>> obtenerSolicitudesPendientes() {
        return ResponseEntity.ok(solicitudRepo.buscarPorEstado("PENDIENTE"));
    }

    @PatchMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<String> aprobarSolicitud(@PathVariable Long id) {
        return solicitudRepo.findById(id).map(solicitud -> {
            solicitud.setEstado_solicitud("APROBADA");
            solicitudRepo.save(solicitud);
            return ResponseEntity.ok("✅ La farmacia " + solicitud.getNombre_fantasia() + " ha sido APROBADA.");
        }).orElse(ResponseEntity.badRequest().body("❌ Solicitud no encontrada."));
    }

    @PatchMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<String> rechazarSolicitud(@PathVariable Long id) {
        return solicitudRepo.findById(id).map(solicitud -> {
            solicitud.setEstado_solicitud("RECHAZADA");
            solicitudRepo.save(solicitud);
            return ResponseEntity.ok("🗑️ La farmacia " + solicitud.getNombre_fantasia() + " ha sido RECHAZADA.");
        }).orElse(ResponseEntity.badRequest().body("❌ Solicitud no encontrada."));
    }

    @PostMapping("/inventario/agregar-manual")
    public ResponseEntity<?> agregarMedicamentoManual(@RequestBody Map<String, Object> payload) {
        try {
            Long idSucursal = Long.valueOf(payload.get("idSucursal").toString());
            String nombre = payload.get("nombre").toString();
            String laboratorio = payload.get("laboratorio").toString();
            BigDecimal precio = new BigDecimal(payload.get("precio").toString());

            SucursalFarmacia sucursal = sucursalFarmaciaRepository.findById(idSucursal)
                    .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));

            Medicamento medicamento = medicamentoRepository.findByNombreCanonico(nombre).orElse(null);
            
            if (medicamento == null) {
                medicamento = new Medicamento();
                medicamento.setNombre_canonico(nombre);
                medicamento.setPrincipio_activo(nombre); 
                medicamento.setOrigen_catalogo("MANUAL");
                medicamento = medicamentoRepository.save(medicamento);
            }

            PrecioVigenteId id = new PrecioVigenteId();
            id.setId_sucursal(sucursal.getId_sucursal());
            id.setTexto_busqueda(medicamento.getNombre_canonico());

            cl.duoc.medimapa.ms_usuarios.model.CorridaActualizacion corrida = new cl.duoc.medimapa.ms_usuarios.model.CorridaActualizacion();
            corrida.setId_fuente(idSucursal);
            corrida.setInicio(java.time.OffsetDateTime.now());
            corrida.setEstado("manual");
            corrida = corridaRepo.save(corrida);

            PrecioVigente pv = new PrecioVigente();
            pv.setId(id);
            pv.setSucursal(sucursal);
            pv.setMedicamento(medicamento);
            pv.setPrecio_max_vta(precio);
            pv.setMoneda("CLP");
            pv.setVigente_desde(java.time.OffsetDateTime.now());
            pv.setCorrida(corrida);

            precioVigenteRepo.save(pv);

            return ResponseEntity.ok("Medicamento guardado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar: " + e.getMessage());
        }
    }

  @DeleteMapping("/inventario/eliminar")
    @Transactional
    public ResponseEntity<?> eliminarMedicamentoInventario(
            @RequestParam Long idSucursal, 
            @RequestParam String nombreMedicamento) {
        try {
            PrecioVigenteId idRelacion = new PrecioVigenteId();
            idRelacion.setId_sucursal(idSucursal);
            idRelacion.setTexto_busqueda(nombreMedicamento);
            
            if (precioVigenteRepo.existsById(idRelacion)) {
                precioVigenteRepo.deleteById(idRelacion);
                precioVigenteRepo.flush();
            }

            Optional<Medicamento> medOpt = medicamentoRepository.findByNombreCanonico(nombreMedicamento);
            
            if (medOpt.isPresent()) {
                medicamentoRepository.delete(medOpt.get());
                medicamentoRepository.flush(); 
                return ResponseEntity.ok("Aniquilado con éxito de todos lados");
            }

            return ResponseEntity.ok("Se eliminó de la farmacia, pero no se encontró en el catálogo global para borrarlo.");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Aviso: " + e.getMessage());
        }
    }

    // 🔥 ESTE ES EL NUEVO ENDPOINT EXCLUSIVO PARA SUBIR EL EXCEL DEL ISP
    @PostMapping("/admin/subir-isp")
    public ResponseEntity<String> subirExcelIsp(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: El archivo Excel está vacío.");
        }
        try {
            String resultado = ispExcelService.sincronizarBioequivalentes(archivo);
            return ResponseEntity.ok("✅ Éxito: " + resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error procesando el Excel del ISP: " + e.getMessage());
        }
    }

    public static class LoginRequest {
        private String correo;
        private String password;

        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}