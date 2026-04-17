package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@Entity
@Table(name = "precio_vigente")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PrecioVigente {

    @EmbeddedId
    private PrecioVigenteId id;

    @Column(name = "precio_max_vta")
    private Double precioMaxVta; 

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "vigente_desde")
    private OffsetDateTime vigenteDesde;

    @ManyToOne(fetch = FetchType.EAGER) // Forzamos la carga para que React siempre vea el medicamento
    @JoinColumn(name = "id_medicamento", insertable = false, updatable = false)
    @JsonIgnoreProperties("precios") // Evita recursión infinita
    private Medicamento medicamento;

    @ManyToOne(fetch = FetchType.EAGER) // Forzamos la carga para que React siempre vea la sucursal
    @JoinColumn(name = "id_sucursal", insertable = false, updatable = false)
    private SucursalFarmacia sucursal;
}