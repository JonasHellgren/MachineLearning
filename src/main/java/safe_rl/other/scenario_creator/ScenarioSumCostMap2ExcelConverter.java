package safe_rl.other.scenario_creator;

import com.google.common.collect.Table;
import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import safe_rl.persistance.trade_environment.PathAndFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log
public class ScenarioSumCostMap2ExcelConverter {

    public static void convertTableToExcel(Map<ScenarioParameters, Double> sumCostMap, PathAndFile pathAndFile) {


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("G2VVersusV2gData");

        // Create the header row
        Row headerRow = sheet.createRow(0);
        int headerCellIndex = 0;
        for (String parName : ScenarioParameters.names()) {
            Cell cell = headerRow.createCell(headerCellIndex++);
            cell.setCellValue(parName);
        }
        headerRow.createCell(headerCellIndex).setCellValue("sumCostDiff");

        // Create the data rows
        int rowIndex = 1;
        for (ScenarioParameters p : sumCostMap.keySet()) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            for (Number num : p.asListOfNumbers()) {
                Cell cell = row.createCell(cellIndex++);

                String value = (num instanceof Double)
                        ? Double.toString((Double) num)
                        : Integer.toString((Integer) num);
                cell.setCellValue(value != null ? value : "");
            }
            double sumCost = sumCostMap.get(p);
            row.createCell(cellIndex).setCellValue(Double.toString(sumCost));
        }

        // Auto-size columns for better readability
        int nofColumns = headerRow.getLastCellNum();
        for (int i = 0; i < nofColumns; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the workbook to the file system
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
}
