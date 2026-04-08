package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventarios")
@Data
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la Farmacia
    @ManyToOne
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;

    // Relación con el Medicamento
    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer precio;

    private Integer stock;
}