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

    @Column(unique = true, nullable = false, length = 400)
    private String nombre_canonico; 

    @Column(length = 400)
    private String principio_activo; 

    @Column(length = 30)
    private String origen_catalogo; 

    @Column(nullable = false)
    private Boolean activo = true; 
}