package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "medicamento_catalogo")
public class MedicamentoCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lo que busca la gente (ej: "Tapsin")
    @Column(name = "nombre_comercial", nullable = false)
    private String nombreComercial;

    // La familia clínica real (ej: "Paracetamol")
    @Column(name = "principio_activo", nullable = false)
    private String principioActivo;
}