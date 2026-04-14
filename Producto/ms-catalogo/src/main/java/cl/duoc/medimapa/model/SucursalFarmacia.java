package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "sucursal_farmacia")
public class SucursalFarmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal") // Nombre en la base de datos
    private Long idSucursal;      // Nombre en Java (camelCase)

    @Column(name = "nombre_sucursal", nullable = false, length = 300)
    private String nombreSucursal; 

    @Column(nullable = false, length = 400)
    private String direccion; 

    // REGLA DE EUGENIO: Columnas decimales separadas (Double) sin PostGIS
    @Column(nullable = false)
    private Double latitud; 

    @Column(nullable = false)
    private Double longitud; 

    private Boolean activo = true; 

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn; 

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}