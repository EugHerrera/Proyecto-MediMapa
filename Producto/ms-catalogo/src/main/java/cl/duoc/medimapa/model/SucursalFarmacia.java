package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

import org.locationtech.jts.geom.Point;

@Data
@Entity
@Table(name = "sucursal_farmacia")
public class SucursalFarmacia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sucursal; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_farmacia", nullable = false)
    private Farmacia farmacia;

    @Column(nullable = false, length = 300)
    private String nombre_sucursal; 

    @Column(nullable = false, length = 400)
    private String direccion; 

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point ubicacion; 

    private Boolean activo = true; 

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn; 

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}