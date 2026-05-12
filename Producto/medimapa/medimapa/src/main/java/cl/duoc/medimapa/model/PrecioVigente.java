package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "precio_vigente")
@Data
public class PrecioVigente {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 


    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private SucursalFarmacia sucursal; 

    @ManyToOne
    @JoinColumn(name = "id_medicamento")
    private Medicamento medicamento; 


    @Column(name = "texto_busqueda", length = 255)
    private String textoBusqueda;

    @Column(precision = 12, scale = 2)
    private BigDecimal precio_max_vta; 

    @Column(length = 3, nullable = false)
    private String moneda = "CLP"; 

    private OffsetDateTime vigente_desde; 

    @ManyToOne
    @JoinColumn(name = "id_corrida", nullable = false)
    private CorridaActualizacion corrida; 
}