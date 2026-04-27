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
        if (regionRepo.count() == 0) {
            
            Region rm = new Region();
            rm.setNombre("Región Metropolitana");
            regionRepo.save(rm);

            Comuna laFlorida = new Comuna();
            laFlorida.setNombreCom("La Florida");
            laFlorida.setRegion(rm);
            comunaRepo.save(laFlorida);

            List<SucursalFarmacia> sucursales = Arrays.asList(
                // 🟦 FARMACIAS AHUMADA (Los 15 Locales validados 🔥)
                crearSucursal("Farmacia Ahumada - Plaza Vespucio L140", "Av. Vicuña Mackenna 7110, Local 140", "-33.5183", "-70.5975", laFlorida),
                crearSucursal("Farmacia Ahumada - Plaza Vespucio L108", "Av. Vicuña Mackenna 7110, Local 108", "-33.5180", "-70.5970", laFlorida),
                crearSucursal("Farmacia Ahumada - Florida Center", "Av. Vicuña Mackenna Oriente 6100", "-33.5111", "-70.6019", laFlorida),
                crearSucursal("Farmacia Ahumada - Jumbo Florida Center", "Av. Vicuña Mackenna Oriente 6100, Local Jumbo", "-33.5115", "-70.6020", laFlorida),
                crearSucursal("Farmacia Ahumada - Santa Amalia", "Av. Santa Amalia 1763", "-33.5413", "-70.5600", laFlorida),
                crearSucursal("Farmacia Ahumada - Enrique Olivares", "Av. La Florida 8434", "-33.5350", "-70.5580", laFlorida),
                crearSucursal("Farmacia Ahumada - Urgencia La Florida", "Av. La Florida 9497", "-33.5435", "-70.5540", laFlorida),
                crearSucursal("Farmacia Ahumada - Vicuña Mackenna 8733", "Av. Vicuña Mackenna 8733-A", "-33.5332", "-70.5925", laFlorida),
                crearSucursal("Farmacia Ahumada - Metro Trinidad", "Av. Vicuña Mackenna 9521", "-33.5445", "-70.5910", laFlorida),
                crearSucursal("Farmacia Ahumada - Walker Martínez", "Av. Walker Martínez 1754", "-33.5235", "-70.5745", laFlorida),
                crearSucursal("Farmacia Ahumada - San José de la Estrella", "Av. Vicuña Mackenna 10955", "-33.5570", "-70.5860", laFlorida),
                crearSucursal("Farmacia Ahumada - Mall Vivo", "Av. La Florida 8988", "-33.5385", "-70.5525", laFlorida),
                crearSucursal("Farmacia Ahumada - Diego Portales", "Av. Diego Portales 1726", "-33.5650", "-70.5590", laFlorida),
                crearSucursal("Farmacia Ahumada - Camilo Henríquez", "Av. Camilo Henríquez 3692", "-33.5600", "-70.5700", laFlorida),
                crearSucursal("Farmacia Ahumada - Rojas Magallanes", "Av. Rojas Magallanes 1305", "-33.5320", "-70.5870", laFlorida),
                
                // 🟦 DR. SIMI (12 Locales)
                crearSucursal("Dr. Simi - Bellavista", "Vicuña Mackenna 7110, Local 114", "-33.5190", "-70.5978", laFlorida),
                crearSucursal("Dr. Simi - Paradero 14", "Av. Vicuña Mackenna 7255", "-33.5185", "-70.5962", laFlorida),
                crearSucursal("Dr. Simi - Metro Trinidad", "Av. Vicuña Mackenna 9531", "-33.5445", "-70.5910", laFlorida),
                crearSucursal("Dr. Simi - San José de la Estrella", "Av. Vicuña Mackenna 11000", "-33.5580", "-70.5850", laFlorida),
                crearSucursal("Dr. Simi - Rojas Magallanes", "Av. Vicuña Mackenna 8500", "-33.5310", "-70.5930", laFlorida),
                crearSucursal("Dr. Simi - Mall Vivo La Florida", "Av. La Florida 8988", "-33.5390", "-70.5520", laFlorida),
                crearSucursal("Dr. Simi - La Florida 9660", "Av. La Florida 9660", "-33.5450", "-70.5540", laFlorida),
                crearSucursal("Dr. Simi - La Florida 8220", "Av. La Florida 8220", "-33.5300", "-70.5490", laFlorida),
                crearSucursal("Dr. Simi - Serafín Zamora", "Serafín Zamora 35", "-33.5205", "-70.5965", laFlorida),
                crearSucursal("Dr. Simi - Vicuña Mackenna Oriente", "Av. Vicuña Mackenna Oriente 7287", "-33.5195", "-70.5955", laFlorida),
                crearSucursal("Dr. Simi - Santa Raquel", "Av. Santa Raquel 10390", "-33.5620", "-70.6120", laFlorida),
                crearSucursal("Dr. Simi - Walker Martínez", "Av. Walker Martínez 1786", "-33.5240", "-70.5750", laFlorida),
                
                // 🟦 SALCOBRAND (14 Locales)
                crearSucursal("Salcobrand - Tienda Dermocoaching", "Vicuña Mackenna N° 7110 TM 129", "-33.5186", "-70.5971", laFlorida),
                crearSucursal("Salcobrand - Paradero 14", "Avda. Vicuña Mackenna 8733, Local 1", "-33.5332", "-70.5925", laFlorida),
                crearSucursal("Salcobrand - Mall Plaza Vespucio (Loc. E)", "Vicuña Mackenna 7110 E-9140", "-33.5180", "-70.5968", laFlorida),
                crearSucursal("Salcobrand - Paseo La Florida", "Vicuña Mackenna Oriente 7110 L-24", "-33.5195", "-70.5972", laFlorida),
                crearSucursal("Salcobrand - Servicentro Bencinera", "Avda. La Florida N° 9871", "-33.5485", "-70.5515", laFlorida),
                crearSucursal("Salcobrand - Mall Florida Center", "Vicuña Mackenna N° 6100", "-33.5115", "-70.6015", laFlorida),
                crearSucursal("Salcobrand - Rojas Magallanes", "Avda. Rojas Magallanes N° 1280", "-33.5320", "-70.5880", laFlorida),
                crearSucursal("Salcobrand - Supermercado Líder", "Avda. Vicuña Mackenna N° 11091", "-33.5585", "-70.5845", laFlorida),
                crearSucursal("Salcobrand - Lider Vespucio", "Avda. Vicuña Mackenna N° 6331", "-33.5135", "-70.6000", laFlorida),
                crearSucursal("Salcobrand - Bellavista", "Avda. Vicuña Mackenna N° 7304", "-33.5200", "-70.5970", laFlorida),
                crearSucursal("Salcobrand - Hospital Clínico Bupa", "Avda. Vicuña Mackenna N° 7747", "-33.5240", "-70.5940", laFlorida),
                crearSucursal("Salcobrand - Mall Plaza Vespucio Ext.", "Avda. Vicuña Mackenna N° 9101", "-33.5365", "-70.5910", laFlorida),
                crearSucursal("Salcobrand - V. Mackenna Local 110", "Vicuña Mackenna Oriente 7110 L-110", "-33.5185", "-70.5975", laFlorida),
                crearSucursal("Salcobrand - Metro Trinidad", "Avda. Vicuña Mackenna N° 9800", "-33.5510", "-70.5890", laFlorida)
            );

            sucursalRepo.saveAll(sucursales);
            System.out.println(">> DataLoader: ¡Éxito! Cargadas 15 Ahumada, 12 Simi y 14 Salcobrand (Total 41 sucursales operativas en La Florida).");
        }
    }

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