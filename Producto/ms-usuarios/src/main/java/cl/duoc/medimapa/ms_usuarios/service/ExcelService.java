package cl.duoc.medimapa.ms_usuarios.service;

import cl.duoc.medimapa.ms_usuarios.model.*;
import cl.duoc.medimapa.ms_usuarios.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Iterator;

@Service
public class ExcelService {

    @Autowired private MedicamentoRepository medicamentoRepo;
    @Autowired private PrecioVigenteRepository precioRepo;
    @Autowired private CorridaActualizacionRepository corridaRepo;
    @Autowired private SucursalFarmaciaRepository sucursalRepo;

    @Transactional 
    public void procesarExcelInventario(MultipartFile file, Long idSucursal) throws Exception {
        
        SucursalFarmacia sucursal = sucursalRepo.findById(idSucursal)
            .orElseThrow(() -> new RuntimeException("No se encontró la sucursal con ID: " + idSucursal));

        CorridaActualizacion corrida = new CorridaActualizacion();
        corrida.setId_fuente(idSucursal);
        corrida.setInicio(OffsetDateTime.now());
        corrida.setEstado("ok");
        corrida = corridaRepo.save(corrida);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                
                if (currentRow.getCell(1) == null) continue;

                String nombreMedicamento = currentRow.getCell(1).getStringCellValue();
                String presentacion = currentRow.getCell(2).getStringCellValue();
                double precioExcel = currentRow.getCell(4).getNumericCellValue();

                String nombreCanonico = nombreMedicamento + " " + presentacion;

                Medicamento med = medicamentoRepo.findByNombreCanonico(nombreCanonico)
                        .orElseGet(() -> {
                            Medicamento nuevo = new Medicamento();
                            nuevo.setNombre_canonico(nombreCanonico);
                            nuevo.setActivo(true);
                            nuevo.setOrigen_catalogo("MANUAL"); 
                            return medicamentoRepo.save(nuevo);
                        });

                PrecioVigente precio = new PrecioVigente();
                // 🔥 GUARDADO LIMPIO SIN LLAVE COMPUESTA
                precio.setTextoBusqueda(nombreCanonico.toLowerCase());
                precio.setMedicamento(med);
                precio.setPrecio_max_vta(BigDecimal.valueOf(precioExcel));
                precio.setCorrida(corrida);
                precio.setVigente_desde(OffsetDateTime.now());
                precio.setSucursal(sucursal);

                precioRepo.save(precio);
            }
            
            corrida.setFin(OffsetDateTime.now());
            corridaRepo.save(corrida);
        }
    }
}