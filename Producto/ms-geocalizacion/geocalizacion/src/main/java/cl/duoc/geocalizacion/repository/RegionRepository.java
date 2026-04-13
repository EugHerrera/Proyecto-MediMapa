package cl.duoc.geocalizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.geocalizacion.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
