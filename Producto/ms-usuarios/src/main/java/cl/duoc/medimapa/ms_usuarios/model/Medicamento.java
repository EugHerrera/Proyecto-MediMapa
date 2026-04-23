package cl.duoc.medimapa.ms_usuarios.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_medicamento; 

    @Column(name = "nombre_canonico", unique = true, nullable = false, length = 400)
    private String nombre_canonico; 

    @Column(name = "principio_activo", length = 400)
    private String principio_activo; 

    @Column(name = "origen_catalogo", length = 30)
    private String origen_catalogo; 

    @Column(nullable = false)
    private Boolean activo = true; 

    // 🔥 EL CAMPO QUE LE FALTABA AL SCRAPER
    @Column(name = "es_bioequivalente")
    private Boolean es_bioequivalente = false;

    // Getters y Setters manuales a prueba de balas
    public void setNombre_canonico(String nombre) { this.nombre_canonico = nombre; }
    public String getNombre_canonico() { return nombre_canonico; }

    public void setPrincipio_activo(String principio) { this.principio_activo = principio; }
    public String getPrincipio_activo() { return principio_activo; }

    public void setOrigen_catalogo(String origen) { this.origen_catalogo = origen; }
    public String getOrigen_catalogo() { return origen_catalogo; }

    public void setEs_bioequivalente(Boolean esBio) { this.es_bioequivalente = esBio; }
    public Boolean getEs_bioequivalente() { return es_bioequivalente; }
}