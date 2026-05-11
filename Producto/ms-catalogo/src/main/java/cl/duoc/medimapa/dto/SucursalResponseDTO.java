package cl.duoc.medimapa.dto;

import lombok.Data;

@Data
public class SucursalResponseDTO {
    private Long idSucursal;
    private String nombreSucursal;
    private String direccion;
    private UbicacionDTO ubicacion; 

    @Data
    public static class UbicacionDTO {
        private String type;
        private Double[] coordinates;
    }
}