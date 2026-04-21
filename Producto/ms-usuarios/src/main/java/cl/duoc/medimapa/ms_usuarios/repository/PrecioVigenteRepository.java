package cl.duoc.medimapa.ms_usuarios.repository;

import cl.duoc.medimapa.ms_usuarios.model.PrecioVigente;
import cl.duoc.medimapa.ms_usuarios.model.PrecioVigenteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrecioVigenteRepository extends JpaRepository<PrecioVigente, PrecioVigenteId> {
    
    // 🔥 MAGIA SQL: Busca sin importar si escriben en mayúsculas o minúsculas
    @Query("SELECT p FROM PrecioVigente p WHERE LOWER(p.id.texto_busqueda) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<PrecioVigente> buscarPorTexto(@Param("texto") String texto);
}