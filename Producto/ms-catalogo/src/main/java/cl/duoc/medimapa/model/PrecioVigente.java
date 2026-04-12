package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "precio_vigente")
@Data
public class PrecioVigente {

    @EmbeddedId
    private PrecioVigenteId id; 

    @ManyToOne
    @MapsId("id_sucursal")
    @JoinColumn(name = "id_sucursal")
    private SucursalFarmacia sucursal; 

    @ManyToOne
    @JoinColumn(name = "id_medicamento")
    private Medicamento medicamento; 

    @Column(precision = 12, scale = 2)
    private BigDecimal precio_max_vta; 

    @Column(length = 3, nullable = false)
    private String moneda = "CLP"; 

    private OffsetDateTime vigente_desde; 

    @ManyToOne
    @JoinColumn(name = "id_corrida", nullable = false)
    private CorridaActualizacion corrida; 
}