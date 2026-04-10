package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_medicamento; 

    @Column(unique = true, nullable = false, length = 400)
    private String nombre_canonico; 

    @Column(length = 400)
    private String principio_activo; 

    @Column(length = 30)
    private String origen_catalogo; 

    @Column(nullable = false)
    private Boolean activo = true; 

    // ==========================================
    // GETTERS Y SETTERS MANUALES (LA VIEJA CONFIABLE)
    // ==========================================

    public String getNombre_canonico() {
        return nombre_canonico;
    }

    public void setNombre_canonico(String nombre_canonico) {
        this.nombre_canonico = nombre_canonico;
    }

    public String getPrincipio_activo() {
        return principio_activo;
    }

    public void setPrincipio_activo(String principio_activo) {
        this.principio_activo = principio_activo;
    }
}