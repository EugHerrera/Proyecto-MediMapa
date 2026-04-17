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

    @Column(name = "nombre_canonico")
    private String nombreCanonico;

    @Column(name = "principio_activo")
    private String principioActivo; 

    @Column(length = 30)
    private String origen_catalogo; 

    @Column(nullable = false)
    private Boolean activo = true;

    // Esta es la columna que nos faltaba y que causaba el error
    @Column(name = "es_bioequivalente")
    private Boolean esBioequivalente = false; 
}