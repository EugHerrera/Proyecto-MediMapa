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

    @Column(name = "laboratorio", length = 200)
    private String laboratorio;

    @Column(name = "origen_catalogo", length = 30)
    private String origen_catalogo; 

    @Column(nullable = false)
    private Boolean activo = true; 

    @Column(name = "es_bioequivalente")
    private Boolean es_bioequivalente = false;

    // --- ESTOS SON LOS CAMPOS NUEVOS QUE FALTABAN ---
    @Column(name = "categoria", length = 150)
    private String categoria;

    @Column(name = "requiere_receta")
    private Boolean requiere_receta = false;

    // --- Getters y Setters Manuales ---
    public void setNombre_canonico(String nombre) { this.nombre_canonico = nombre; }
    public String getNombre_canonico() { return nombre_canonico; }

    public void setPrincipio_activo(String principio) { this.principio_activo = principio; }
    public String getPrincipio_activo() { return principio_activo; }

    public void setLaboratorio(String laboratorio) { this.laboratorio = laboratorio; }
    public String getLaboratorio() { return laboratorio; }

    public void setOrigen_catalogo(String origen) { this.origen_catalogo = origen; }
    public String getOrigen_catalogo() { return origen_catalogo; }

    public void setEs_bioequivalente(Boolean esBio) { this.es_bioequivalente = esBio; }
    public Boolean getEs_bioequivalente() { return es_bioequivalente; }

    // --- GETTERS Y SETTERS DE LOS CAMPOS NUEVOS ---
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getCategoria() { return categoria; }

    public void setRequiere_receta(Boolean requiere_receta) { this.requiere_receta = requiere_receta; }
    public Boolean getRequiere_receta() { return requiere_receta; }
}