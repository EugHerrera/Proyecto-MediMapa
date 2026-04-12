package cl.duoc.geocalizacion.model;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.geolatte.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
@Entity
@Table(name = "sucursal_farmacia")

public class SucursalFarmacia {
@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sucursal; // Igualamos el nombre del ID al de Eugenio

    @Column(nullable = false, length = 300)
    private String nombre_sucursal;

    @Column(nullable = false, length = 400)
    private String direccion;

    // Cambiamos Geometría por números decimales
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
