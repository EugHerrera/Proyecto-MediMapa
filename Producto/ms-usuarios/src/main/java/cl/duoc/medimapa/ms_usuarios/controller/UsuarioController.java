package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.security.JwtUtil;
import cl.duoc.medimapa.ms_usuarios.dto.LoginRequest;
import cl.duoc.medimapa.ms_usuarios.model.Usuario;
import cl.duoc.medimapa.ms_usuarios.repository.UsuarioRepository;
import cl.duoc.medimapa.ms_usuarios.service.ExcelService;
import cl.duoc.medimapa.ms_usuarios.repository.PrecioVigenteRepository;
import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import cl.duoc.medimapa.ms_usuarios.model.SolicitudInscripcion; // 🔥 Importación necesaria

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private PrecioVigenteRepository precioVigenteRepo;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private JwtUtil jwtUtil; 

    @Autowired
    private ExcelService excelService; 
    
    @Autowired
    private cl.duoc.medimapa.ms_usuarios.repository.SolicitudInscripcionRepository solicitudRepo;

    // --- REGISTRO Y LOGIN (Intactos) ---
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

    // --- SUBIDA DE INVENTARIO CONECTADA AL SERVICIO ---
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

    // --- 1. ENDPOINT PARA LEER EL INVENTARIO REAL Y MANDARLO A LA TABLA ---
    @GetMapping("/inventario/listar/{idSucursal}")
    public ResponseEntity<List<Map<String, Object>>> obtenerInventarioReal(@PathVariable Long idSucursal) {
        List<PrecioVigente> inventario = precioVigenteRepo.buscarPorSucursal(idSucursal);
        
        List<Map<String, Object>> respuesta = inventario.stream().map(precio -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", precio.getMedicamento().getId_medicamento()); 
            map.put("nombre", precio.getMedicamento().getNombreCanonico());
            map.put("precio", precio.getPrecio_max_vta());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(respuesta);
    }

    // --- 2. ENDPOINT PARA GUARDAR EL NUEVO PRECIO MANUAL ---
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

    // =====================================================================
    // 🔥 LÓGICA DE GESTIÓN DE SOLICITUDES DE FARMACIAS INDEPENDIENTES 🔥
    // =====================================================================

    // A. RECIBIR FORMULARIO PÚBLICO
    @PostMapping("/solicitud-inscripcion")
    public ResponseEntity<String> recibirSolicitud(@RequestBody SolicitudInscripcion nuevaSolicitud) {
        try {
            nuevaSolicitud.setEstado_solicitud("PENDIENTE");
            nuevaSolicitud.setFecha_solicitud(java.time.OffsetDateTime.now());
            
            if (nuevaSolicitud.getAcepta_ley_21719() == null || !nuevaSolicitud.getAcepta_ley_21719()) {
                return ResponseEntity.badRequest().body("❌ Error: Debe aceptar la Ley 21.719 para procesar los datos.");
            }

            solicitudRepo.save(nuevaSolicitud);
            return ResponseEntity.ok("✅ Solicitud enviada con éxito. Será revisada por un administrador.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al guardar la solicitud: " + e.getMessage());
        }
    }

    // B. TRAER SOLICITUDES PENDIENTES AL ADMIN
    @GetMapping("/solicitudes/pendientes")
    public ResponseEntity<List<SolicitudInscripcion>> obtenerSolicitudesPendientes() {
        List<SolicitudInscripcion> pendientes = solicitudRepo.buscarPorEstado("PENDIENTE");
        return ResponseEntity.ok(pendientes);
    }

    // C. APROBAR SOLICITUD
    @PatchMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<String> aprobarSolicitud(@PathVariable Long id) {
        return solicitudRepo.findById(id).map(solicitud -> {
            solicitud.setEstado_solicitud("APROBADA");
            solicitudRepo.save(solicitud);
            return ResponseEntity.ok("✅ La farmacia " + solicitud.getNombre_fantasia() + " ha sido APROBADA.");
        }).orElse(ResponseEntity.badRequest().body("❌ Solicitud no encontrada."));
    }

    // D. RECHAZAR SOLICITUD
    @PatchMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<String> rechazarSolicitud(@PathVariable Long id) {
        return solicitudRepo.findById(id).map(solicitud -> {
            solicitud.setEstado_solicitud("RECHAZADA");
            solicitudRepo.save(solicitud);
            return ResponseEntity.ok("🗑️ La farmacia " + solicitud.getNombre_fantasia() + " ha sido RECHAZADA.");
        }).orElse(ResponseEntity.badRequest().body("❌ Solicitud no encontrada."));
    }
}