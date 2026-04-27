/*package cl.duoc.geocalizacion.config;

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
        // 1. Regenerar todos los datos cada vez que arranca la aplicación
        sucursalRepo.deleteAll();
        comunaRepo.deleteAll();
        regionRepo.deleteAll();

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
                crearSucursal("Dr. Simi - Vicuña Mackenna 7287", "Avda. Vicuña Mackenna 7287", "-33.5202092", "-70.5987675", laFlorida),
                crearSucursal("Dr. Simi - La Florida 9660", "Avda. La Florida 9660", "-33.5428264", "-70.5697048", laFlorida),
                crearSucursal("Dr. Simi - Walker Martínez 1786", "Walker Martínez N°1786", "-33.5221014", "-70.5795070", laFlorida),
                crearSucursal("Dr. Simi - La Florida 8220", "Avda. La Florida N°8220", "-33.5281624", "-70.5755567", laFlorida),
                crearSucursal("Dr. Simi - Vicuña Mackenna 7110 Local 15 Boulevard", "Avda. Vicuña Mackenna N°7110 Local 15 Boulevard", "-33.5165652", "-70.5999735", laFlorida),
                crearSucursal("Dr. Simi - La Florida 9073 Local 3", "Avenida La Florida 9073 local 3", "-33.5344348", "-70.5745221", laFlorida),
                crearSucursal("Dr. Simi - Serafín Zamora 35", "Serafín Zamora 35", "-33.5211529", "-70.6005452", laFlorida),
                crearSucursal("Dr. Simi - Vicuña Mackenna 7110 Local D-104", "Avda Vicuña Mackenna N°7110 Local D-104", "-33.5178218", "-70.5981728", laFlorida),
                crearSucursal("Dr. Simi - Vicuña Mackenna 7110 Local D-105", "Avda Vicuña Mackenna N°7110 Local D-105", "-33.5175921", "-70.5954112", laFlorida),
                crearSucursal("Dr. Simi - Vicuña Mackenna 7110 Local M-1", "Avda Vicuña Mackenna N°7110 local M-1", "-33.5193287", "-70.5997773", laFlorida),
                crearSucursal("Dr. Simi - La Florida 8988", "Avda La Florida 8988", "-33.5352988", "-70.5723002", laFlorida),
                crearSucursal("Salcobrand - Mall Plaza Vespucio", "Av. Vicuña Mackenna 7110, Local 120", "-33.5215", "-70.5970", laFlorida),
                crearSucursal("Salcobrand - Florida Center", "Av. Vicuña Mackenna 6100, Local 1050", "-33.5210", "-70.5990", laFlorida),
                crearSucursal("Salcobrand - Paseo La Florida", "Av. La Florida 9301", "-33.5445", "-70.5535", laFlorida),
                crearSucursal("Salcobrand - Metro Trinidad", "Av. Vicuña Mackenna 9800", "-33.5510", "-70.5890", laFlorida),
                crearSucursal("Salcobrand - Servicentro La Florida", "Avda. La Florida N° 9871, Servicentro", "-33.5481970", "-70.5685689", laFlorida),
                crearSucursal("Salcobrand - Mall / Strip Center 6100", "Avda. Vicuña Mackenna N° 6100 Mall / Strip Center", "-33.5105496", "-70.6083241", laFlorida),
                crearSucursal("Salcobrand - Mall / Strip Center Rojas Magallanes 1280", "Avda. Rojas Magallanes N° 1280 Mall / Strip Center", "-33.5354549", "-70.5739931", laFlorida),
                crearSucursal("Salcobrand - Mall / Strip Center Vicuña Mackenna 11091", "Avda. Vicuña Mackenna N° 11091 Mall / Strip Center", "-33.5609251", "-70.5860475", laFlorida),
                crearSucursal("Salcobrand - Supermercado Vicuña Mackenna 6331", "Avda. Vicuña Mackenna N° 6331 L-5, Supermercado", "-33.5192220", "-70.5998213", laFlorida),
                crearSucursal("Salcobrand - Local 24-25", "Avda.Vicuña Mackenna N° 7304 - 7308 Local 24 - 25", "-33.5254997", "-70.5977248", laFlorida),
                crearSucursal("Salcobrand - Vicuña Mackenna 9101", "Avda. Vicuña Mackenna N° 9101", "-33.5398648", "-70.5916431", laFlorida),
                crearSucursal("Salcobrand - Local 110 Mall / Strip Center", "Avda. Vicuña Mackenna Oriente N° 7110 Local 110 Mall / Strip Center", "-33.51760177", "-70.5980852", laFlorida),
                crearSucursal("Salcobrand - Local A", "Avda. Departamental N° 1455 Local A Hospital / Clínica / Centro Medico La Florida", "-33.5101675", "-70.5965847", laFlorida),
                crearSucursal("Salcobrand - TM 129 Vicuña Mackenna 7110", "Avda. Vicuña Mackenna N° 7110 TM 129", "-33.5193173", "-70.5997566", laFlorida),
                crearSucursal("Salcobrand - Local 1", "Avda. Vicuña Mackenna 8733 Local 1", "-33.5354859", "-70.5934514", laFlorida),
                crearSucursal("Salcobrand - Locales E-9140/E-9136/E-9132/E-9128", "Avda. Vicuña Mackenna N° 7110 Locales E-9140, E-9136, E-9132, E-9128", "-33.5193173", "-70.5997566", laFlorida),
                crearSucursal("Ahumada - Vicuña Mackenna 7110 Local 12", "Vicuña Mackenna N° 7110 local Nº12", "-33.5180275", "-70.5977102", laFlorida),
                crearSucursal("Ahumada - Vicuña Mackenna 7110 Locales E-9109/E-9112", "Avda. Vicuña Mackenna N° 7110 Local E-9109 E-9112", "-33.5178889", "-70.5978765", laFlorida),
                crearSucursal("Ahumada - Froilán Roa 7107", "Froilán Roa N° 7107", "-33.5162832", "-70.59781877", laFlorida),
                crearSucursal("Ahumada - Vicuña Mackenna 7196", "Avda. Vicuña Mackenna N° 7196", "-33.5193445", "-70.6021772", laFlorida),
                crearSucursal("Ahumada - Américo Vespucio 7310", "Avda. Américo Vespucio N° 7310", "-33.5196461", "-70.5954295", laFlorida),
                crearSucursal("Ahumada - Vicuña Mackenna 6100 Local 102", "Avda. Vicuña Mackenna N° 6100 L. 102", "-33.5106191", "-70.6061243", laFlorida),
                crearSucursal("Ahumada - Américo Vespucio 6325", "Avda. Américo Vespucion N° 6325", "-33.5118741", "-70.5917070", laFlorida),
                crearSucursal("Ahumada - Vicuña Mackenna 9521", "Avda. Vicuña Mackenna N° 9521", "-33.5435570", "-70.5896604", laFlorida),
                crearSucursal("Ahumada - La Florida 9497", "Avda. La Florida N° 9497", "-33.5432607", "-70.5705468", laFlorida),
                crearSucursal("Ahumada - Santa Amalia Supermercado Líder", "Avda. Santa Amalia N° 1763 / Supermercado Líder", "-33.5449876", "-70.5708940", laFlorida),
                crearSucursal("Ahumada - Rojas Magallanes 3638", "Rojas Magallanes N° 3638", "-33.5354752", "-70.5558007", laFlorida),
                crearSucursal("Ahumada - Walker Martínez 3600", "Avda. Walker Martínez Nº 3600", "-33.5218443", "-70.5587105", laFlorida),
                crearSucursal("Ahumada - La Florida 8988", "Avda. La Florida 8988", "-33.5349448", "-70.5720425", laFlorida)
        );

        sucursalRepo.saveAll(sucursales);
        System.out.println(">> DataLoader: Base de datos regenerada con éxito (Región, Comuna y 40 Sucursales).\n");
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
}*/