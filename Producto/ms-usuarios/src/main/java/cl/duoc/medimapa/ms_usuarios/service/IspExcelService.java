package cl.duoc.medimapa.ms_usuarios.service;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import cl.duoc.medimapa.ms_usuarios.repository.MedicamentoRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class IspExcelService {

    @Autowired
    private MedicamentoRepository medicamentoRepo;

    public String sincronizarBioequivalentes(MultipartFile file) throws Exception {
        // 🔥 WorkbookFactory lee automáticamente .xls y .xlsx sin reventar
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int totalActualizados = 0;

        int colNombre = -1;
        Row headerRow = sheet.getRow(0); 
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().toLowerCase().contains("nombre producto")) {
                colNombre = cell.getColumnIndex();
                break;
            }
        }

        if (colNombre == -1) {
            workbook.close();
            throw new Exception("No se encontró la columna 'Nombre Producto' en el Excel.");
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(colNombre);
            if (cell == null) continue;

            String nombreIsp = cell.getStringCellValue().trim();
            Optional<Medicamento> medOpt = medicamentoRepo.findByNombreCanonico(nombreIsp);
            
            if (medOpt.isPresent()) {
                Medicamento m = medOpt.get();
                if (!m.getEs_bioequivalente()) {
                    m.setEs_bioequivalente(true);
                    medicamentoRepo.save(m);
                    totalActualizados++;
                }
            }
        }
        
        workbook.close();
        return "Sincronización completa. Se certificaron " + totalActualizados + " medicamentos.";
    }
}