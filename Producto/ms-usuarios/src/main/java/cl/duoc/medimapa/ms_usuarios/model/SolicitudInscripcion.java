package cl.duoc.medimapa.ms_usuarios.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "solicitud_inscripcion")
public class SolicitudInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_solicitud;

    private String nombre_fantasia;
    private String razon_social;
    private String rut_empresa;
    private String resolucion_seremi;
    private String region;
    private String comuna;
    private String direccion;

    private String rep_legal_nombre;
    private String rep_legal_rut;
    private String rep_legal_correo;
    private String rep_legal_telefono;

    private String quimico_nombre;
    private String quimico_rut;
    private String quimico_correo;

    private Boolean acepta_ley_21719;
    private OffsetDateTime fecha_solicitud;
    private String estado_solicitud; // PENDIENTE, APROBADA, RECHAZADA

    // --- GETTERS Y SETTERS ---
    // (Genera los getters y setters aquí con tu IDE o pega los de abajo)

    public Long getId_solicitud() { return id_solicitud; }
    public void setId_solicitud(Long id_solicitud) { this.id_solicitud = id_solicitud; }

    public String getNombre_fantasia() { return nombre_fantasia; }
    public void setNombre_fantasia(String nombre_fantasia) { this.nombre_fantasia = nombre_fantasia; }

    public String getRazon_social() { return razon_social; }
    public void setRazon_social(String razon_social) { this.razon_social = razon_social; }

    public String getRut_empresa() { return rut_empresa; }
    public void setRut_empresa(String rut_empresa) { this.rut_empresa = rut_empresa; }

    public String getResolucion_seremi() { return resolucion_seremi; }
    public void setResolucion_seremi(String resolucion_seremi) { this.resolucion_seremi = resolucion_seremi; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getRep_legal_nombre() { return rep_legal_nombre; }
    public void setRep_legal_nombre(String rep_legal_nombre) { this.rep_legal_nombre = rep_legal_nombre; }

    public String getRep_legal_rut() { return rep_legal_rut; }
    public void setRep_legal_rut(String rep_legal_rut) { this.rep_legal_rut = rep_legal_rut; }

    public String getRep_legal_correo() { return rep_legal_correo; }
    public void setRep_legal_correo(String rep_legal_correo) { this.rep_legal_correo = rep_legal_correo; }

    public String getRep_legal_telefono() { return rep_legal_telefono; }
    public void setRep_legal_telefono(String rep_legal_telefono) { this.rep_legal_telefono = rep_legal_telefono; }

    public String getQuimico_nombre() { return quimico_nombre; }
    public void setQuimico_nombre(String quimico_nombre) { this.quimico_nombre = quimico_nombre; }

    public String getQuimico_rut() { return quimico_rut; }
    public void setQuimico_rut(String quimico_rut) { this.quimico_rut = quimico_rut; }

    public String getQuimico_correo() { return quimico_correo; }
    public void setQuimico_correo(String quimico_correo) { this.quimico_correo = quimico_correo; }

    public Boolean getAcepta_ley_21719() { return acepta_ley_21719; }
    public void setAcepta_ley_21719(Boolean acepta_ley_21719) { this.acepta_ley_21719 = acepta_ley_21719; }

    public OffsetDateTime getFecha_solicitud() { return fecha_solicitud; }
    public void setFecha_solicitud(OffsetDateTime fecha_solicitud) { this.fecha_solicitud = fecha_solicitud; }

    public String getEstado_solicitud() { return estado_solicitud; }
    public void setEstado_solicitud(String estado_solicitud) { this.estado_solicitud = estado_solicitud; }
}