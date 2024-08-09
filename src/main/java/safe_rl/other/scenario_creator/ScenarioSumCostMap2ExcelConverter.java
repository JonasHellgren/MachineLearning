package safe_rl.other.scenerio_table;

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
import java.util.Set;

@Log
public class ScenarioTable2ExcelConverter {

    public static void convertTableToExcel(Table<Integer, Integer, String> table, PathAndFile pathAndFile) {


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Table Data");
        Set<Integer> colKeys = table.columnKeySet();


        // Create the header row
        Row headerRow = sheet.createRow(0);
        int headerCellIndex = 0;
        for (Integer columnKey : colKeys) {
            Cell cell = headerRow.createCell(headerCellIndex++);
            cell.setCellValue(columnKey.toString());
        }

        // Create the data rows
        int rowIndex = 1;
        for (Integer rowKey : table.rowKeySet()) {
            Row row = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            Cell rowHeaderCell = row.createCell(cellIndex++);
            rowHeaderCell.setCellValue(rowKey);

            for (Integer columnKey : colKeys) {
                Cell cell = row.createCell(cellIndex++);
                String value = table.get(rowKey, columnKey);
                cell.setCellValue(value != null ? value : "");
            }
        }

        // Auto-size columns for better readability
        for (int i = 0; i < colKeys.size(); i++) {
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
