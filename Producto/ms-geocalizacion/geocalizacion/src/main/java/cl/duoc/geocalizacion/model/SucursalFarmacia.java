package cl.duoc.geocalizacion.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sucursal_farmacia")

public class SucursalFarmacia { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Long id;

    private String nombre_sucursal;
    private String direccion;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Boolean activo;

    // Ahora sí los constructores coinciden perfectamente con la clase
    public SucursalFarmacia() {}

    public SucursalFarmacia(String nombre_sucursal, String direccion, BigDecimal latitud, BigDecimal longitud, Boolean activo) {
        this.nombre_sucursal = nombre_sucursal;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre_sucursal() {
        return nombre_sucursal;
    }

    public void setNombre_sucursal(String nombre_sucursal) {
        this.nombre_sucursal = nombre_sucursal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getLatitud() {
        return latitud;
    }

    public void setLatitud(BigDecimal latitud) {
        this.latitud = latitud;
    }

    public BigDecimal getLongitud() {
        return longitud;
    }

    public void setLongitud(BigDecimal longitud) {
        this.longitud = longitud;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
