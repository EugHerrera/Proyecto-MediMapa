package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "medicamento")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Evita errores de carga perezosa
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long id_medicamento; 

    @Column(name = "nombre_canonico")
    private String nombreCanonico;

    @Column(name = "principio_activo")
    private String principioActivo; 

    @Column(name = "origen_catalogo", length = 30)
    private String origen_catalogo; 

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "es_bioequivalente")
    private Boolean esBioequivalente = false; 
}