package cl.duoc.medimapa.dto;

import lombok.Data;

@Data
public class MedicamentoResponseDTO {
    private Long idMedicamento;
    private String nombreCanonico;
    private String principioActivo;
    
    // ---> ESTE ES EL CAMPO QUE EXIGE TU SERVICIO <---
    private String categoria; 
    
    private Boolean esBioequivalente;
}