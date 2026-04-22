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

    @Column(name = "nombre_canonico", unique = true, nullable = false, length = 400)
    private String nombreCanonico;

    @Column(name = "principio_activo", length = 400)
    private String principioActivo;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "origen_catalogo", length = 30)
    private String origenCatalogo;

    @Column(name = "es_bioequivalente")
    private Boolean esBioequivalente;

    @Column(nullable = false)
    private Boolean activo = true;
}