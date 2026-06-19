package cl.duoc.medimapa.ms_usuarios.service;

import cl.duoc.medimapa.ms_usuarios.model.Medicamento;
import cl.duoc.medimapa.ms_usuarios.repository.MedicamentoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CatalogoMasivoService {

    @Autowired
    private MedicamentoRepository medicamentoRepo;

    public String procesarExcelIsp(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Medicamento> nuevosMedicamentos = new ArrayList<>();
            Set<String> nombresYaVistos = new HashSet<>(); // ESCUDO ANTI-DUPLICADOS
            DataFormatter formatter = new DataFormatter(); // FORMATEADOR BLINDADO
            int agregados = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar cabecera

                Cell cellNombre = row.getCell(1);
                Cell cellPrincipio = row.getCell(4);

                if (cellNombre == null || cellPrincipio == null) continue;

                // Convierte cualquier tipo de celda a texto puro
                String nombre = formatter.formatCellValue(cellNombre).trim();
                String principioActivo = formatter.formatCellValue(cellPrincipio).trim();
                
                if (nombre.isEmpty() || principioActivo.isEmpty()) continue;

                // Si ya procesamos este nombre en el excel, lo ignoramos para no chocar
                if (nombresYaVistos.contains(nombre)) {
                    continue;
                }

                if (medicamentoRepo.findByNombreCanonico(nombre).isEmpty()) {
                    nombresYaVistos.add(nombre); // Lo anotamos en memoria temporal

                    Medicamento med = new Medicamento();
                    med.setNombre_canonico(nombre);
                    med.setPrincipio_activo(principioActivo);
                    med.setCategoria(inferirCategoria(principioActivo)); 
                    med.setActivo(true);
                    med.setOrigen_catalogo("ISP_EXCEL");
                    med.setRequiere_receta(false); 
                    med.setEs_bioequivalente(false); 
                    
                    // Ya que estamos, rescatemos también el Laboratorio (Columna 3)
                    Cell cellLab = row.getCell(3);
                    if (cellLab != null) {
                        med.setLaboratorio(formatter.formatCellValue(cellLab).trim());
                    }

                    nuevosMedicamentos.add(med);
                    agregados++;
                }

                // Guardar en lotes
                if (nuevosMedicamentos.size() >= 500) {
                    medicamentoRepo.saveAll(nuevosMedicamentos);
                    nuevosMedicamentos.clear();
                }
            }
            
            // Guardar el saldo final
            if (!nuevosMedicamentos.isEmpty()) {
                medicamentoRepo.saveAll(nuevosMedicamentos);
            }

            return "✅ Carga Masiva Exitosa: Se categorizaron y agregaron " + agregados + " nuevos fármacos a MediMapa.";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error procesando el Excel: " + e.getMessage();
        }
    }

    private String inferirCategoria(String principio) {
        String p = principio.toUpperCase();
        
        if (p.contains("AMOXICILINA") || p.contains("AZITROMICINA") || p.contains("CIPROFLOXACINO") || p.contains("CLARITROMICINA") || p.contains("PENICILINA")) {
            return "Antibióticos y antibacterianos";
        }
        if (p.contains("PARACETAMOL") || p.contains("IBUPROFENO") || p.contains("DICLOFENACO") || p.contains("KETOROLACO") || p.contains("NAPROXENO") || p.contains("TRAMADOL") || p.contains("MELOXICAM")) {
            return "Analgésicos y antiinflamatorios";
        }
        if (p.contains("LORATADINA") || p.contains("DESLORATADINA") || p.contains("CETIRIZINA") || p.contains("CHLORFENAMINA") || p.contains("FEXOFENADINA")) {
            return "Antialérgicos";
        }
        if (p.contains("LOSARTAN") || p.contains("ATORVASTATINA") || p.contains("ENALAPRIL") || p.contains("AMLODIPINO") || p.contains("CARVEDILOL")) {
            return "Cardiovascular y circulación";
        }
        if (p.contains("OMEPRAZOL") || p.contains("LANSOPRAZOL") || p.contains("DOMPERIDONA") || p.contains("PANTOPRAZOL") || p.contains("ESOMEPRAZOL")) {
            return "Digestivo y gastrointestinal";
        }
        if (p.contains("SERTRALINA") || p.contains("CLONAZEPAM") || p.contains("FLUOXETINA") || p.contains("ZOPICLONA") || p.contains("ESCITALOPRAM") || p.contains("QUETIAPINA")) {
            return "Salud mental y neurológico";
        }
        if (p.contains("METFORMINA") || p.contains("INSULINA") || p.contains("GLIBENCLAMIDA")) {
            return "Antidiabéticos y metabolismo";
        }
        
        return "Otros"; 
    }
}