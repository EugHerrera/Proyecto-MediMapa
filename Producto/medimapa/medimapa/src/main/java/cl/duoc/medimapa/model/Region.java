package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "regiones")
@Data
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_region;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Región Metropolitana de Santiago"
}