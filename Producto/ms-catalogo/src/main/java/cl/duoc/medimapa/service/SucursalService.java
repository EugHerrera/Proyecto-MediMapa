package cl.duoc.medimapa.service;

import cl.duoc.medimapa.dto.SucursalResponseDTO; // <-- AQUÍ ESTÁ EL IMPORT SOLUCIONADO
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
                    // SE CORRIGIERON LOS GUIONES BAJOS PARA QUE LOMBOK NO LLORE
                    dto.setIdSucursal(suc.getId_sucursal()); 
                    dto.setNombreSucursal(suc.getNombre_sucursal()); 
                    dto.setDireccion(suc.getDireccion());
                    dto.setUbicacion(suc.getUbicacion());
                    return dto;
                }).collect(Collectors.toList());
    }
}