package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medicamentos")
@Data
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_comercial", nullable = false)
    private String nombreComercial;

    @Column(name = "principio_activo", nullable = false)
    private String principioActivo;

    @Column(name = "es_bioequivalente", nullable = false)
    private Boolean esBioequivalente;

    private String gramaje;

    @Column(name = "requiere_receta")
    private Boolean requiereReceta;
}