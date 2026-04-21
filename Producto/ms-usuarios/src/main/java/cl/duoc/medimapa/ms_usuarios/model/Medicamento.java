package cl.duoc.medimapa.ms_usuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_medicamento; 

    // Aquí le decimos que en la BD se llama con guion bajo, 
    // pero en Java la usaremos con CamelCase
    @Column(name = "nombre_canonico", unique = true, nullable = false, length = 400)
    private String nombreCanonico; 

    @Column(name = "principio_activo", length = 400)
    private String principioActivo; 

    @Column(name = "origen_catalogo", length = 30)
    private String origenCatalogo; 

    @Column(nullable = false)
    private Boolean activo = true; 
}