package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point; // ¡IMPORTANTE: Que sea esta importación!

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "sucursal_farmacia")
public class SucursalFarmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sucursal; 

    @Column(nullable = false, length = 300)
    private String nombre_sucursal; 

    @Column(nullable = false, length = 400)
    private String direccion; 

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal latitud; 

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal longitud; 

    private Boolean activo = true; 

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn; 

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}