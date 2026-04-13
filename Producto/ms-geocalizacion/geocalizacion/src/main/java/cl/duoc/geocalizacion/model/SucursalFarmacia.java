package cl.duoc.geocalizacion.model;


import java.math.BigDecimal;
import java.time.OffsetDateTime;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sucursal_farmacia")
@Data
public class SucursalFarmacia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_sucursal; //

    @Column(nullable = false, length = 300)
    private String nombre_sucursal; 

    @Column(nullable = false, length = 400)
    private String direccion; 

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal latitud; //

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal longitud; //

    private Boolean activo = true; 

    // Relación para geolocalización (tu valor agregado)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_comuna")
    private Comuna comuna;

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn; 

    @Column(name = "actualizado_en")
    private OffsetDateTime actualizadoEn;
}
