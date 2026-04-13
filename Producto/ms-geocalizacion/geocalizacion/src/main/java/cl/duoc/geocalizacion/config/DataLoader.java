package cl.duoc.geocalizacion.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.geocalizacion.model.Comuna;
import cl.duoc.geocalizacion.model.Region;
import cl.duoc.geocalizacion.model.SucursalFarmacia;
import cl.duoc.geocalizacion.repository.ComunaRepository;
import cl.duoc.geocalizacion.repository.RegionRepository;
import cl.duoc.geocalizacion.repository.SucursalFarmaciaRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final RegionRepository regionRepo;
    private final ComunaRepository comunaRepo;
    private final SucursalFarmaciaRepository sucursalRepo;

    public DataLoader(RegionRepository regionRepo, ComunaRepository comunaRepo, SucursalFarmaciaRepository sucursalRepo) {
        this.regionRepo = regionRepo;
        this.comunaRepo = comunaRepo;
        this.sucursalRepo = sucursalRepo;
    }

@Override
    public void run(String... args) throws Exception {
        // 1. Verificar si ya existen datos para evitar duplicados
        if (regionRepo.count() == 0) {
            
            // 2. Crear Región Metropolitana
            Region rm = new Region();
            rm.setNombre("Región Metropolitana");
            regionRepo.save(rm);

            // 3. Crear Comuna de La Florida
            Comuna laFlorida = new Comuna();
            laFlorida.setNombreCom("La Florida");
            laFlorida.setRegion(rm);
            comunaRepo.save(laFlorida);

            // 4. Cargar todas las sucursales de La Florida
            List<SucursalFarmacia> sucursales = Arrays.asList(
                crearSucursal("Farmacia Ahumada - Plaza Vespucio", "Av. Vicuña Mackenna 7110, Local 140", "-33.5208", "-70.5982", laFlorida),
                crearSucursal("Farmacia Ahumada - Florida Center", "Av. Vicuña Mackenna Oriente 6100", "-33.5212", "-70.5985", laFlorida),
                crearSucursal("Farmacia Ahumada - Santa Amalia", "Av. La Florida 9210", "-33.5431", "-70.5542", laFlorida),
                crearSucursal("Farmacia Ahumada - Enrique Olivares", "Av. La Florida 8434", "-33.5350", "-70.5580", laFlorida),
                crearSucursal("Dr. Simi - Bellavista", "Vicuña Mackenna 7110, Local 114", "-33.5221", "-70.5978", laFlorida),
                crearSucursal("Dr. Simi - Paradero 14", "Av. Vicuña Mackenna 7255", "-33.5185", "-70.5962", laFlorida),
                crearSucursal("Dr. Simi - San José de la Estrella", "Av. Vicuña Mackenna 11000", "-33.5580", "-70.5850", laFlorida),
                crearSucursal("Dr. Simi - Rojas Magallanes", "Av. Vicuña Mackenna 8500", "-33.5310", "-70.5930", laFlorida),
                crearSucursal("Salcobrand - Mall Plaza Vespucio", "Av. Vicuña Mackenna 7110, Local 120", "-33.5215", "-70.5970", laFlorida),
                crearSucursal("Salcobrand - Florida Center", "Av. Vicuña Mackenna 6100, Local 1050", "-33.5210", "-70.5990", laFlorida),
                crearSucursal("Salcobrand - Paseo La Florida", "Av. La Florida 9301", "-33.5445", "-70.5535", laFlorida),
                crearSucursal("Salcobrand - Metro Trinidad", "Av. Vicuña Mackenna 9800", "-33.5510", "-70.5890", laFlorida)
            );

            sucursalRepo.saveAll(sucursales);
            System.out.println(">> DataLoader: Base de datos poblada con éxito (Región, Comuna y 12 Sucursales).");
        }
    }

    // Método auxiliar para no repetir código
    private SucursalFarmacia crearSucursal(String nombre, String direccion, String lat, String lon, Comuna comuna) {
        SucursalFarmacia sucursal = new SucursalFarmacia();
        sucursal.setNombre_sucursal(nombre);
        sucursal.setDireccion(direccion);
        sucursal.setLatitud(new BigDecimal(lat));
        sucursal.setLongitud(new BigDecimal(lon));
        sucursal.setComuna(comuna);
        sucursal.setActivo(true);
        return sucursal;
    }
}