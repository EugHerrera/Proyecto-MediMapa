package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long idMedicamento;
    
    @Column(name = "nombre_canonico")
    private String nombreCanonico;
    
    @Column(name = "principio_activo")
    private String principioActivo;
    
    @Column(name = "origen_catalogo")
    private String origenCatalogo;
    
    @Column(name = "es_bioequivalente")
    private Boolean esBioequivalente; // Agregado según regla de Eugenio
    
    private Boolean activo;
}