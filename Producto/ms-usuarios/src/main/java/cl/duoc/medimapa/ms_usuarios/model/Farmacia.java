package cl.duoc.medimapa.ms_usuarios.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "farmacias")
@Data

public class Farmacia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;



}
