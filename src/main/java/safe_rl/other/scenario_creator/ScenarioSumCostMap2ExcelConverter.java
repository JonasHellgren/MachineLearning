package safe_rl.other.scenario_creator;

import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import safe_rl.persistance.trade_environment.PathAndFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Log
public class ScenarioSumCostMap2ExcelConverter {

    private ScenarioSumCostMap2ExcelConverter() {
    }

    public static void convertTableToExcel(Map<ScenarioParameters, Double> sumCostMap, PathAndFile pathAndFile) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("G2VVersusV2gData");
        var headerRow = createHeader(sheet);
        createDataRows(sumCostMap, sheet);
        autoSizeColumns(sheet, headerRow);
        saveWorkBook(pathAndFile, workbook);
    }

    private static void saveWorkBook(PathAndFile pathAndFile, Workbook workbook) {
        try (FileOutputStream fileOut = new FileOutputStream(pathAndFile.fullName())) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void autoSizeColumns(Sheet sheet, Row headerRow) {
        int nofColumns = headerRow.getLastCellNum();
        for (int i = 0; i < nofColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createDataRows(Map<ScenarioParameters, Double> sumCostMap, Sheet sheet) {
        int rowIndex = 1;
        for (ScenarioParameters p : sumCostMap.keySet()) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (Number num : p.asListOfNumbers()) {
                String value = (num instanceof Double)
                        ? Double.toString((Double) num)
                        : Integer.toString((Integer) num);
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue(value != null ? value : "");
            }
            double sumCost = sumCostMap.get(p);
            row.createCell(cellIndex).setCellValue(Double.toString(sumCost));
        }
    }

    @NotNull
    private static Row createHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        int headerCellIndex = 0;
        for (String parName : ScenarioParameters.names()) {
            Cell cell = headerRow.createCell(headerCellIndex++);
            cell.setCellValue(parName);
        }
        headerRow.createCell(headerCellIndex).setCellValue("sumCostDiff");
        return headerRow;
    }
}
