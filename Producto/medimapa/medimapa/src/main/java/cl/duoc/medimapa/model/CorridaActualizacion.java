package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "corrida_actualizacion")
@Data
public class CorridaActualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_corrida; // Coincide con int8(64) 

    @Column(nullable = false)
    private Long id_fuente; // Referencia a la fuente de datos (ej: Salcobrand) [cite: 133, 141]

    @Column(nullable = false)
    private OffsetDateTime inicio; // Fecha y hora de inicio del scraping 

    private OffsetDateTime fin; // Fecha y hora de término 

    @Column(nullable = false, length = 20)
    private String estado; // Debe ser 'ok', 'error' o 'parcial' 

    @Column(columnDefinition = "text")
    private String detalle_error; // Para registrar qué falló en el bot 
}