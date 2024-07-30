package safe_rl.persistance.trade_environment;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Log
public class ElPriceXlsRead {
    ElPriceRepo repo;


    public void readDataFromFile(PathAndFile file, ElType type) {
        var map = createSheetMap(file);
        map.entrySet().forEach(e -> System.out.println(e));

    }

    public void clear() {
        repo.clear();
    }

    Map<String, Table<Integer, Integer, Double>> createSheetMap(PathAndFile file) {

        Map<String, Table<Integer, Integer, Double>> sheetTableMap = new HashMap<>();

        try (
                InputStream inputStream = new FileInputStream(file.fullName());
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            log.info("Reading from file " + file + ", includes nof sheets=" + workbook.getNumberOfSheets());
            for (Sheet sheet : workbook) {
                var nofRowsAndCols = nofRowsAndCols(sheet);
                String sheetName = sheet.getSheetName();
                sheetTableMap.put(sheetName, getTable(sheet));
                log.info("Sheet=" + sheetName + ", nofRowsAndCols = " + nofRowsAndCols);
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return sheetTableMap;
    }

    private static Pair<Integer, Integer> nofRowsAndCols(Sheet sheet) {
        int numberOfRows = sheet.getLastRowNum() + 1;
        int numberOfColumns = 0;
        for (Row row : sheet) {
            if (row.getLastCellNum() > numberOfColumns) {
                numberOfColumns = row.getLastCellNum();
            }
        }
        return Pair.create(numberOfRows, numberOfColumns);
    }

    private static Table<Integer, Integer, Double> getTable(Sheet sheet) {
        Table<Integer, Integer, Double> table = HashBasedTable.create();
        for (Row row : sheet) {
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING -> log.info("String data excluded, sheet=" + sheet.getSheetName() + ", cell=" + cell);
                    case NUMERIC -> table.put(cell.getRowIndex(), cell.getColumnIndex(), cell.getNumericCellValue());
                    default -> log.warning("Unknown cell type, sheet=" + sheet.getSheetName());
                }
            }
        }
        return table;
    }


}
