package cl.duoc.medimapa.ms_usuarios.controller;

import cl.duoc.medimapa.ms_usuarios.security.JwtUtil;
import cl.duoc.medimapa.ms_usuarios.dto.LoginRequest;
import cl.duoc.medimapa.ms_usuarios.model.Usuario;
import cl.duoc.medimapa.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Importante para el Excel

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Autowired
    private JwtUtil jwtUtil; 

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

    // --- NUEVO: APROBACIÓN DE FARMACIAS (Para Súper Admin) ---
    @PatchMapping("/aprobar/{id}")
    public ResponseEntity<String> aprobarFarmacia(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(usuario -> {
            // Aquí podrías cambiar un campo 'activo' o similar
            // usuario.setActivo(true); 
            // usuarioRepository.save(usuario);
            return ResponseEntity.ok("Farmacia " + usuario.getCorreo() + " aprobada correctamente.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- NUEVO: SUBIDA DE INVENTARIO (Para Farmacéutico) ---
    @PostMapping("/inventario/subir")
    public ResponseEntity<String> subirInventario(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) return ResponseEntity.badRequest().body("Archivo vacío.");
        // Aquí procesarías el archivo con Apache POI o similar
        return ResponseEntity.ok("Archivo '" + archivo.getOriginalFilename() + "' recibido y en proceso de carga.");
    }
}