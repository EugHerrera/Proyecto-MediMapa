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

    @Transactional // 🔥 Asegura que si algo falla, no se guarden datos incompletos
    public void procesarExcelInventario(MultipartFile file, Long idSucursal) throws Exception {
        
        // 1. Buscamos la sucursal primero para validar que existe antes de empezar
        SucursalFarmacia sucursal = sucursalRepo.findById(idSucursal)
            .orElseThrow(() -> new RuntimeException("No se encontró la sucursal con ID: " + idSucursal));

        // 2. Creamos la "Corrida" para el historial
        CorridaActualizacion corrida = new CorridaActualizacion();
        corrida.setId_fuente(idSucursal);
        corrida.setInicio(OffsetDateTime.now());
        corrida.setEstado("ok");
        corrida = corridaRepo.save(corrida);

        // 3. Abrimos el Excel
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Saltamos la cabecera
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                
                // Validar que la fila no esté vacía
                if (currentRow.getCell(1) == null) continue;

                // Leemos los datos (Asegúrate que el Excel siga el orden: SKU, Nombre, Presentación, Stock, Precio)
                String nombreMedicamento = currentRow.getCell(1).getStringCellValue();
                String presentacion = currentRow.getCell(2).getStringCellValue();
                double precioExcel = currentRow.getCell(4).getNumericCellValue();

                String nombreCanonico = nombreMedicamento + " " + presentacion;

                // 4. Buscar o Crear Medicamento
                Medicamento med = medicamentoRepo.findByNombreCanonico(nombreCanonico)
                        .orElseGet(() -> {
                            Medicamento nuevo = new Medicamento();
                            nuevo.setNombreCanonico(nombreCanonico);
                            nuevo.setActivo(true);
                            nuevo.setOrigenCatalogo("MANUAL"); // Ajustado a lo que vimos en tu PostgreSQL
                            return medicamentoRepo.save(nuevo);
                        });

                // 5. Guardar/Actualizar Precio Vigente
                PrecioVigente precio = new PrecioVigente();
                
                PrecioVigenteId idCompuesto = new PrecioVigenteId();
                idCompuesto.setId_sucursal(idSucursal);
                idCompuesto.setTexto_busqueda(nombreCanonico.toLowerCase());
                
                precio.setId(idCompuesto);
                precio.setMedicamento(med);
                precio.setPrecio_max_vta(BigDecimal.valueOf(precioExcel));
                precio.setCorrida(corrida);
                precio.setVigente_desde(OffsetDateTime.now());
                precio.setSucursal(sucursal);

                precioRepo.save(precio);
            }
            
            // 6. Finalizamos la corrida
            corrida.setFin(OffsetDateTime.now());
            corridaRepo.save(corrida);
        }
    }
}   