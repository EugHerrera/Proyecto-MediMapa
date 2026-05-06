package cl.duoc.medimapa.model;


import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "cadena_farmacia")
@Data
public class Farmacia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Salcobrand", "Cruz Verde"
}
