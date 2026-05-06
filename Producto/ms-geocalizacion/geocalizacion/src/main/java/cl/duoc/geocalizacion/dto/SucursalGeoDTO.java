package cl.duoc.geocalizacion.dto;

import lombok.Data;

@Data
public class SucursalGeoDTO {
    private Long id_sucursal;
    private String nombre_sucursal;
    private String direccion;
    private UbicacionDTO ubicacion;
    private String comunaNombre;

    @Data
    public static class UbicacionDTO {
        private String type;
        private Double[] coordinates;
    }
}
