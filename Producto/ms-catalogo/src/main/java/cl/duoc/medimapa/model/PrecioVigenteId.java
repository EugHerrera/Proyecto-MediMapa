package cl.duoc.medimapa.model;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PrecioVigenteId implements Serializable {
    private Long id_sucursal;
    private String texto_busqueda;
}