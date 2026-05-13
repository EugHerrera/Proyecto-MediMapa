package cl.duoc.medimapa.ms_usuarios.service;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import cl.duoc.medimapa.ms_usuarios.repository.MedicamentoRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class IspExcelService {

    @Autowired
    private MedicamentoRepository medicamentoRepo;

    public String sincronizarBioequivalentes(MultipartFile file) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        List<Medicamento> catalogo = medicamentoRepo.findAll();
        int totalActualizados = 0;

        int colNombre = -1;
        int colPrincipio = -1;
        Row headerRow = sheet.getRow(0); 
        
        // Buscamos dinámicamente las columnas clave del ISP
        for (Cell cell : headerRow) {
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String valorCelda = cell.getStringCellValue().trim().toLowerCase();
                if (valorCelda.equals("nombre") || valorCelda.contains("nombre producto")) {
                    colNombre = cell.getColumnIndex();
                }
                if (valorCelda.contains("principio activo") || valorCelda.equals("principio")) {
                    colPrincipio = cell.getColumnIndex();
                }
            }
        }

        if (colNombre == -1) {
            workbook.close();
            throw new Exception("No se encontró la columna de Nombres en el Excel del ISP.");
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Cell cellNombre = row.getCell(colNombre);
            if (cellNombre == null) continue;

            String textoIsp = "";
            if (cellNombre.getCellType() == CellType.STRING) {
                textoIsp += cellNombre.getStringCellValue().trim().toLowerCase();
            } else if (cellNombre.getCellType() == CellType.NUMERIC) {
                textoIsp += String.valueOf(cellNombre.getNumericCellValue()).trim().toLowerCase();
            }

            if (colPrincipio != -1) {
                Cell cellPrincipio = row.getCell(colPrincipio);
                if (cellPrincipio != null && cellPrincipio.getCellType() == CellType.STRING) {
                    textoIsp += " " + cellPrincipio.getStringCellValue().trim().toLowerCase();
                }
            }

            if (textoIsp.isEmpty()) continue;

            for (Medicamento m : catalogo) {
                if (!m.getEs_bioequivalente()) {
                    String nomDb = m.getNombre_canonico() != null ? m.getNombre_canonico().toLowerCase() : "";
                    String prinDb = m.getPrincipio_activo() != null ? m.getPrincipio_activo().toLowerCase() : "";

                    if ((!nomDb.isEmpty() && textoIsp.contains(nomDb)) || 
                        (!prinDb.isEmpty() && textoIsp.contains(prinDb))) {
                        
                        m.setEs_bioequivalente(true);
                        medicamentoRepo.save(m);
                        totalActualizados++;
                    }
                }
            }
        }
        
        workbook.close();
        return " Sincronización completa. Se certificaron " + totalActualizados + " medicamentos de tu catálogo.";
    }
}