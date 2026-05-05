package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sucursal_farmacia")
@Data
public class SucursalFarmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sucursal; 

    // Relación clave: ¡Esta sucursal pertenece a una Farmacia (Marca)!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_farmacia", nullable = false)
    private Farmacia farmacia;

    @Column(nullable = false, length = 300)
    private String nombre_sucursal; // Ej: "Local Vicuña Mackenna 1234" o "Mall Plaza Vespucio"

    @Column(nullable = false, length = 400)
    private String direccion; 

    // Aquí traemos la magia de PostGIS (Reemplaza a los BigDecimal de lat/lon)
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point ubicacion; 

    private Boolean activo = true; 

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn; 

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}