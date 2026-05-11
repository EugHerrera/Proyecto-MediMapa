package cl.duoc.medimapa.service;

import cl.duoc.medimapa.dto.SucursalResponseDTO; 
import cl.duoc.medimapa.repository.SucursalFarmaciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SucursalService {

    @Autowired
    private SucursalFarmaciaRepository repo;

    public List<SucursalResponseDTO> listarSucursalesActivas() {
        return repo.findByActivoTrue().stream()
                .map(suc -> {
                    SucursalResponseDTO dto = new SucursalResponseDTO();
                    dto.setIdSucursal(suc.getId_sucursal()); 
                    dto.setNombreSucursal(suc.getNombre_sucursal()); 
                    dto.setDireccion(suc.getDireccion());
                    
                    // 🔥 Desarmamos el Point para que el frontend lo entienda
                    if (suc.getUbicacion() != null) {
                        SucursalResponseDTO.UbicacionDTO ubiDto = new SucursalResponseDTO.UbicacionDTO();
                        ubiDto.setType("Point");
                        ubiDto.setCoordinates(new Double[]{suc.getUbicacion().getX(), suc.getUbicacion().getY()});
                        dto.setUbicacion(ubiDto);
                    }
                    
                    return dto;
                }).collect(Collectors.toList());
    }
}