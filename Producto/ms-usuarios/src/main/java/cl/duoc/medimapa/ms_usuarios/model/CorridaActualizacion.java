package cl.duoc.medimapa.ms_usuarios.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "corrida_actualizacion")
@Data
public class CorridaActualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_corrida; 

    @Column(nullable = false)
    private Long id_fuente; 

    @Column(nullable = false)
    private OffsetDateTime inicio; // Fecha y hora de inicio del scraping 

    private OffsetDateTime fin; // Fecha y hora de término 

    @Column(nullable = false, length = 20)
    private String estado; 

    @Column(columnDefinition = "text")
    private String detalle_error; // Para registrar qué falló en el bot 
}