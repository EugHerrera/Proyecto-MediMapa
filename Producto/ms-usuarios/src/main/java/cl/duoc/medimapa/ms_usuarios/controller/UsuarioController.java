package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.security.JwtUtil;
import cl.duoc.medimapa.ms_usuarios.model.*;
import cl.duoc.medimapa.ms_usuarios.repository.*;
import cl.duoc.medimapa.ms_usuarios.service.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
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
    @Autowired private CatalogoMasivoService catalogoMasivoService;
    @Autowired private CorridaActualizacionRepository corridaRepo;

    // --- LOGIN Y REGISTRO ---
    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(nuevoUsuario.getPasswordHash()));
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok("Usuario registrado con éxito.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());
        Map<String, String> response = new HashMap<>();

        if (usuarioOpt.isPresent() && passwordEncoder.matches(request.getPassword(), usuarioOpt.get().getPasswordHash())) {
            Usuario u = usuarioOpt.get();
            response.put("token", jwtUtil.generarToken(u.getCorreo(), u.getRol()));
            response.put("rol", u.getRol());
            return ResponseEntity.ok(response);
        }
        response.put("error", "Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // --- GESTIÓN CATÁLOGO MAESTRO (SÚPER ADMIN) ---
    @GetMapping("/medicamentos-admin")
    public List<Medicamento> listarTodo() {
        return medicamentoRepository.findAll();
    }

    @PostMapping("/medicamentos-admin")
    public ResponseEntity<Medicamento> crearMedicamento(@RequestBody Medicamento nuevoMedicamento) {
        nuevoMedicamento.setOrigen_catalogo("ADMIN_WEB");
        nuevoMedicamento.setActivo(true);
        return ResponseEntity.ok(medicamentoRepository.save(nuevoMedicamento));
    }

    @PostMapping("/admin/subir-isp")
    public ResponseEntity<String> subirExcelIsp(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) return ResponseEntity.badRequest().body("Archivo vacío.");
        try {
            return ResponseEntity.ok(catalogoMasivoService.procesarExcelIsp(archivo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/medicamentos-admin/{id}")
    public ResponseEntity<Void> eliminarMedicamentoGlobal(@PathVariable Long id) {
        medicamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- INVENTARIO FARMACÉUTICO ---
    @PostMapping("/inventario/subir")
    public ResponseEntity<String> subirInventario(@RequestParam("archivo") MultipartFile archivo, @RequestParam(defaultValue = "99") Long idSucursal) {
        try {
            excelService.procesarExcelInventario(archivo, idSucursal);
            return ResponseEntity.ok("Inventario actualizado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/inventario/listar/{idSucursal}")
    public ResponseEntity<List<Map<String, Object>>> obtenerInventarioReal(@PathVariable Long idSucursal) {
        List<PrecioVigente> inventario = precioVigenteRepo.buscarPorSucursal(idSucursal);
        return ResponseEntity.ok(inventario.stream().map(precio -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", precio.getMedicamento().getId_medicamento());
            map.put("nombre", precio.getMedicamento().getNombre_canonico());
            map.put("precio", precio.getPrecio_max_vta());
            map.put("laboratorio", precio.getMedicamento().getLaboratorio());
            return map;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/inventario/actualizar-precio")
    public ResponseEntity<String> actualizarPrecioManual(@RequestParam Long idSucursal, @RequestParam String textoBusqueda, @RequestParam Double nuevoPrecio) {
        return precioVigenteRepo.buscarPorSucursal(idSucursal).stream()
                .filter(p -> p.getTextoBusqueda() != null && p.getTextoBusqueda().equalsIgnoreCase(textoBusqueda))
                .findFirst().map(precio -> {
                    precio.setPrecio_max_vta(BigDecimal.valueOf(nuevoPrecio));
                    precioVigenteRepo.save(precio);
                    return ResponseEntity.ok("Actualizado");
                }).orElse(ResponseEntity.badRequest().body("No encontrado"));
    }

    // --- SOLICITUDES ---
    @PostMapping("/solicitud-inscripcion")
    public ResponseEntity<String> recibirSolicitud(@RequestBody SolicitudInscripcion nuevaSolicitud) {
        nuevaSolicitud.setEstado_solicitud("PENDIENTE");
        nuevaSolicitud.setFecha_solicitud(OffsetDateTime.now());
        solicitudRepo.save(nuevaSolicitud);
        return ResponseEntity.ok("Solicitud enviada.");
    }

    @GetMapping("/solicitudes/pendientes")
    public ResponseEntity<List<SolicitudInscripcion>> obtenerSolicitudesPendientes() {
        return ResponseEntity.ok(solicitudRepo.buscarPorEstado("PENDIENTE"));
    }

    @GetMapping("/solicitudes/{id}/aprobar")
    @Transactional
    public ResponseEntity<String> aprobarSolicitud(@PathVariable Long id) {
        return solicitudRepo.findById(id).map(sol -> {
            sol.setEstado_solicitud("APROBADA");
            solicitudRepo.save(sol);
            // Crear sucursal
            SucursalFarmacia suc = new SucursalFarmacia();
            suc.setNombre_sucursal(sol.getNombre_fantasia());
            suc.setDireccion(sol.getDireccion());
            suc.setUbicacion(new GeometryFactory().createPoint(new Coordinate(0.0, 0.0)));
            sucursalFarmaciaRepository.save(suc);
            // Crear usuario
            Usuario usr = new Usuario();
            usr.setCorreo(sol.getQuimico_correo());
            usr.setPasswordHash(passwordEncoder.encode(sol.getQuimico_rut()));
            usr.setRol("FARMACEUTICO");
            usuarioRepository.save(usr);
            return ResponseEntity.ok("Aprobada");
        }).orElse(ResponseEntity.badRequest().body("Error"));
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