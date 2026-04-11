package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comunas")
@Data
public class Comuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_comuna;

    @Column(nullable = false)
    private String nombre; // Ej: "La Florida"

    @ManyToOne
    @JoinColumn(name = "id_region", nullable = false)
    private Region region;
}