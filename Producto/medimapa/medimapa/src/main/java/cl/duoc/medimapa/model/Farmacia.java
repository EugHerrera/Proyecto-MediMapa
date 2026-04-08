package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "farmacias")
@Data

public class Farmacia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    // ¡Aquí está la magia de PostGIS para guardar la latitud y longitud en el mapa!
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point ubicacion;
    
}
