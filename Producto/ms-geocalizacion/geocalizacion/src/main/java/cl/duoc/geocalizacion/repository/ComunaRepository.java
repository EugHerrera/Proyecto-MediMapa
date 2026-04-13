package cl.duoc.geocalizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.geocalizacion.model.Comuna;

public interface ComunaRepository extends JpaRepository<Comuna, Long> {
}
