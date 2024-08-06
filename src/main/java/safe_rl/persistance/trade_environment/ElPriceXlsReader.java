package safe_rl.persistance.trade_environment;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.arrow.flatbuf.Int;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Injects data from xlsx file into repo
 *  Every column in xlsx file is assumed to include price data, and represent one day
 *
 * A lot of hassle before working, needed following dependency
 *         <dependency>
 *             <groupId>commons-io</groupId>
 *             <artifactId>commons-io</artifactId>
 *             <version>2.16.1</version>
 *         </dependency>
 */

@AllArgsConstructor
@Log
public class ElPriceXlsReader {
    ElPriceRepo repo;

    public static ElPriceXlsReader of(ElPriceRepo repo) {
        return new ElPriceXlsReader(repo);
    }

    public void readDataFromFile(PathAndFile file, ElType type) {
        var map = createSheetMap(file);

        for (Map.Entry<String, Table<Integer, Integer, Double>> entry: map.entrySet()) {
            String sheetName = entry.getKey();
            var table=entry.getValue();
            log.info("Adding price data for sheet="+ sheetName);
            for (Integer day:table.columnKeySet()) {
                var nameDecoder = new SheetNameDecoder(sheetName);
                var id = DayId.of(nameDecoder.year(), nameDecoder.month(), day, nameDecoder.region());
                var prices = table.column(day);
                List<Double> pricesAllHours = prices.values().stream().toList();
                var priceData = PriceData.of(id, type, pricesAllHours);
                log.fine("..for day="+day);
                repo.addDataForDay(priceData);
            }
        }
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
                    default -> log.fine("Unknown cell type, sheet=" + sheet.getSheetName());
                }
            }
        }
        return table;
    }


}
