package cl.duoc.medimapa.dto;

import lombok.Data;

@Data
public class MedicamentoResponseDTO {
    private Long idMedicamento;
    private String nombreCanonico;
    private String principioActivo;
    
    private String categoria; 
    
    private Boolean esBioequivalente;
}