package cl.duoc.medimapa.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SucursalResponseDTO {
    private Long idSucursal;
    private String nombreSucursal;
    private String direccion;
    private BigDecimal latitud;
    private BigDecimal longitud;
}