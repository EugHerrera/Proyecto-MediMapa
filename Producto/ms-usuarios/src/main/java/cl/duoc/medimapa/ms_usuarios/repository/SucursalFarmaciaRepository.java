package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.SucursalFarmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SucursalFarmaciaRepository extends JpaRepository<SucursalFarmacia, Long> {
}