package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "interacciones")
@Data
public class Interaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El primer medicamento involucrado
    @ManyToOne
    @JoinColumn(name = "medicamento_uno_id", nullable = false)
    private Medicamento medicamentoUno;

    // El segundo medicamento que causa la reacción
    @ManyToOne
    @JoinColumn(name = "medicamento_dos_id", nullable = false)
    private Medicamento medicamentoDos;

    @Column(name = "nivel_gravedad", nullable = false)
    private String nivelGravedad; // Ej: "Leve", "Moderado", "Grave"

    @Column(nullable = false, length = 500)
    private String descripcion; // El texto de alerta para el usuario
}