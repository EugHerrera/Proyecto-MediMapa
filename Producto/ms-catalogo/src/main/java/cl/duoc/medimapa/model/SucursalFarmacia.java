package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point; // ¡IMPORTANTE: Que sea esta importación!
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "sucursal_farmacia")
public class SucursalFarmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long idSucursal;

    @Column(name = "nombre_sucursal", nullable = false, length = 300)
    private String nombreSucursal; 

    @Column(nullable = false, length = 400)
    private String direccion; 

    // REGLA DE EUGENIO: Usamos Point de PostGIS con SRID 4326 (GPS Estándar)
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point ubicacion; 

    private Boolean activo = true; 

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn; 

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}