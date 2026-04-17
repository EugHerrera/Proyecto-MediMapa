package cl.duoc.medimapa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "precio_vigente")
@Data
public class PrecioVigente {

    @EmbeddedId
    private PrecioVigenteId id;

    @Column(name = "precio_max_vta")
    private Double precioMaxVta; // Nombre limpio para la Query

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "vigente_desde")
    private OffsetDateTime vigenteDesde;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", insertable = false, updatable = false)
    private Medicamento medicamento;

    @ManyToOne
    @JoinColumn(name = "id_sucursal", insertable = false, updatable = false)
    private SucursalFarmacia sucursal;
}