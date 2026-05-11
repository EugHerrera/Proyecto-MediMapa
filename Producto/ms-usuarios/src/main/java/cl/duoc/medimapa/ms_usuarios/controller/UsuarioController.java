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
import cl.duoc.medimapa.ms_usuarios.repository.CorridaActualizacionRepository;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import cl.duoc.medimapa.ms_usuarios.model.SolicitudInscripcion; 
import cl.duoc.medimapa.ms_usuarios.model.SucursalFarmacia;
import cl.duoc.medimapa.ms_usuarios.model.CorridaActualizacion;

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
    @Autowired private IspExcelService ispExcelService; 
    @Autowired private CorridaActualizacionRepository corridaRepo;

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
        if (archivo.isEmpty()) return ResponseEntity.badRequest().body("Archivo vacío.");
        try {
            excelService.procesarExcelInventario(archivo, idSucursal);
            return ResponseEntity.ok("✅ Archivo '" + archivo.getOriginalFilename() + "' procesado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error: " + e.getMessage());
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
            map.put("laboratorio", precio.getMedicamento().getLaboratorio()); 
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
                .filter(p -> p.getTextoBusqueda() != null && p.getTextoBusqueda().equalsIgnoreCase(textoBusqueda))
                .findFirst()
                .map(precio -> {
                    precio.setPrecio_max_vta(BigDecimal.valueOf(nuevoPrecio));
                    precio.setVigente_desde(OffsetDateTime.now());
                    precioVigenteRepo.save(precio);
                    return ResponseEntity.ok("✅ Precio actualizado a $" + nuevoPrecio);
                })
                .orElse(ResponseEntity.badRequest().body("❌ No se encontró el medicamento en esta sucursal."));
    }

    @PatchMapping("/inventario/actualizar-nombre")
    @Transactional 
    public ResponseEntity<String> actualizarNombreMedicamento(
            @RequestParam("idMedicamento") Long idMedicamento,
            @RequestParam("nuevoNombre") String nuevoNombre) {
        
        return medicamentoRepository.findById(idMedicamento).map(med -> {
            med.setNombre_canonico(nuevoNombre);
            medicamentoRepository.save(med);

            List<PrecioVigente> preciosRelacionados = precioVigenteRepo.findAll().stream()
                .filter(p -> p.getMedicamento() != null && p.getMedicamento().getId_medicamento().equals(idMedicamento))
                .collect(Collectors.toList());

            for (PrecioVigente pv : preciosRelacionados) {
                pv.setTextoBusqueda(nuevoNombre);
                precioVigenteRepo.save(pv);
            }

            return ResponseEntity.ok("✅ Nombre actualizado correctamente en catálogo e inventario.");
        }).orElse(ResponseEntity.badRequest().body("❌ No se encontró el medicamento en el catálogo."));
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
                medicamento.setLaboratorio(laboratorio); 
                medicamento.setPrincipio_activo(nombre); 
                medicamento.setOrigen_catalogo("MANUAL");
                medicamento = medicamentoRepository.save(medicamento);
            }

            CorridaActualizacion corrida = new CorridaActualizacion();
            corrida.setId_fuente(idSucursal);
            corrida.setInicio(OffsetDateTime.now());
            corrida.setEstado("manual");
            corrida = corridaRepo.save(corrida);

            PrecioVigente pv = new PrecioVigente();
            pv.setTextoBusqueda(medicamento.getNombre_canonico());
            pv.setSucursal(sucursal);
            pv.setMedicamento(medicamento);
            pv.setPrecio_max_vta(precio);
            pv.setMoneda("CLP");
            pv.setVigente_desde(OffsetDateTime.now());
            pv.setCorrida(corrida);

            precioVigenteRepo.save(pv);

            return ResponseEntity.ok("Medicamento guardado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar: " + e.getMessage());
        }
    }

    @DeleteMapping("/inventario/eliminar")
    public ResponseEntity<?> eliminarMedicamentoInventario(
            @RequestParam Long idSucursal, 
            @RequestParam String nombreMedicamento) {
        try {
            List<PrecioVigente> inventarioLocal = precioVigenteRepo.buscarPorSucursal(idSucursal);
            PrecioVigente precioLocal = inventarioLocal.stream()
                .filter(p -> {
                    boolean matchNombre = p.getMedicamento() != null && 
                                          p.getMedicamento().getNombre_canonico().equalsIgnoreCase(nombreMedicamento);
                    boolean matchBusqueda = p.getTextoBusqueda() != null && 
                                            p.getTextoBusqueda().equalsIgnoreCase(nombreMedicamento);
                    return matchNombre || matchBusqueda;
                })
                .findFirst()
                .orElse(null);

            if (precioLocal == null) return ResponseEntity.badRequest().body("❌ Medicamento no encontrado.");

            Medicamento medVinculado = precioLocal.getMedicamento();
            Long idLocal = precioLocal.getId();

            if (medVinculado != null && "MANUAL".equalsIgnoreCase(medVinculado.getOrigen_catalogo())) {
                List<PrecioVigente> todasLasDependencias = precioVigenteRepo.findAll().stream()
                    .filter(p -> p.getMedicamento() != null && p.getMedicamento().getId_medicamento().equals(medVinculado.getId_medicamento()))
                    .collect(Collectors.toList());
                
                precioVigenteRepo.deleteAll(todasLasDependencias);
                medicamentoRepository.deleteById(medVinculado.getId_medicamento());
                return ResponseEntity.ok("✅ Eliminado de raíz (Medicamento MANUAL).");
            } else {
                precioVigenteRepo.deleteById(idLocal);
                return ResponseEntity.ok("✅ Eliminado de tu sucursal local.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error: " + e.getMessage());
        }
    }

    @PostMapping("/solicitud-inscripcion")
    public ResponseEntity<String> recibirSolicitud(@RequestBody SolicitudInscripcion nuevaSolicitud) {
        try {
            nuevaSolicitud.setEstado_solicitud("PENDIENTE");
            nuevaSolicitud.setFecha_solicitud(OffsetDateTime.now());
            if (nuevaSolicitud.getAcepta_ley_21719() == null || !nuevaSolicitud.getAcepta_ley_21719()) {
                return ResponseEntity.badRequest().body("❌ Debe aceptar la Ley 21.719.");
            }
            solicitudRepo.save(nuevaSolicitud);
            return ResponseEntity.ok("✅ Solicitud enviada.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error: " + e.getMessage());
        }
    }

    @GetMapping("/solicitudes/pendientes")
    public ResponseEntity<List<SolicitudInscripcion>> obtenerSolicitudesPendientes() {
        return ResponseEntity.ok(solicitudRepo.buscarPorEstado("PENDIENTE"));
    }

    @PatchMapping("/solicitudes/{id}/aprobar")
    @Transactional 
    public ResponseEntity<String> aprobarSolicitud(@PathVariable Long id) {
        return solicitudRepo.findById(id).map(solicitud -> {
            solicitud.setEstado_solicitud("APROBADA");
            solicitudRepo.save(solicitud);

            SucursalFarmacia nuevaSucursal = new SucursalFarmacia();
            nuevaSucursal.setNombre_sucursal(solicitud.getNombre_fantasia());
            nuevaSucursal.setDireccion(solicitud.getDireccion() + ", " + solicitud.getComuna()); 
            
            GeometryFactory geometryFactory = new GeometryFactory();
            org.locationtech.jts.geom.Point puntoVacio = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
            puntoVacio.setSRID(4326);
            nuevaSucursal.setUbicacion(puntoVacio);
            
            nuevaSucursal.setActivo(true);
            nuevaSucursal.setCreadoEn(OffsetDateTime.now());
            nuevaSucursal.setActualizadoEn(OffsetDateTime.now());
            sucursalFarmaciaRepository.save(nuevaSucursal);

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setCorreo(solicitud.getQuimico_correo());
            nuevoUsuario.setPasswordHash(passwordEncoder.encode(solicitud.getQuimico_rut())); 
            nuevoUsuario.setRol("FARMACEUTICO");
            usuarioRepository.save(nuevoUsuario);

            return ResponseEntity.ok("✅ Farmacia aprobada. Acceso creado para: " + solicitud.getQuimico_correo());
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

    @PostMapping("/admin/subir-isp")
    public ResponseEntity<String> subirExcelIsp(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) return ResponseEntity.badRequest().body("Archivo ISP vacío.");
        try {
            String resultado = ispExcelService.sincronizarBioequivalentes(archivo);
            return ResponseEntity.ok("✅ Éxito: " + resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error ISP: " + e.getMessage());
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