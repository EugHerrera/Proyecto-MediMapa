package cl.duoc.medimapa.dto;

import org.geolatte.geom.Point;
import lombok.Data;

@Data
public class SucursalResponseDTO {
    private Long idSucursal;
    private String nombreSucursal;
    private String direccion;
    private Point ubicacion;
}