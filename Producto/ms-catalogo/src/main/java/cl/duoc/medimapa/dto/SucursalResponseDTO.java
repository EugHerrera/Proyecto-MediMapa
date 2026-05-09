package cl.duoc.medimapa.dto;

// 🔥 CORRECCIÓN: Usamos LocationTech JTS para evitar choques de serialización
import org.locationtech.jts.geom.Point;
import lombok.Data;

@Data
public class SucursalResponseDTO {
    private Long idSucursal;
    private String nombreSucursal;
    private String direccion;
    private Point ubicacion;
}