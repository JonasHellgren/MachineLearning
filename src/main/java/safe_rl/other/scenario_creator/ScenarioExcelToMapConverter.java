package safe_rl.other.scenario_creator;

import lombok.extern.java.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import safe_rl.persistance.trade_environment.PathAndFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log
public class ScenarioExcelToMapConverter {

    private ScenarioExcelToMapConverter() {
    }

    public static Map<ScenarioParameters, Double> readExcelToMap(PathAndFile pathAndFile) {
        Map<ScenarioParameters, Double> sumCostMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(pathAndFile.fullName());
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            for (Row row : sheet) {
                if (isHeader) {
                    isHeader = false;  // Skip the header row
                    continue;
                }
                var scenarioParameters = parseScenarioParameters(row);
                Double sumCost = parseSumCost(row);
                sumCostMap.put(scenarioParameters, sumCost);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sumCostMap;
    }

    private static ScenarioParameters parseScenarioParameters(Row row) {
        int cellIndex = 0;
        double priceBattery = Double.parseDouble(row.getCell(cellIndex++).getStringCellValue());
        double priceHWAddOn = Double.parseDouble(row.getCell(cellIndex++).getStringCellValue());
        double socStart = Double.parseDouble(row.getCell(cellIndex++).getStringCellValue());
        double powerChargeMax = Double.parseDouble(row.getCell(cellIndex++).getStringCellValue());
        int dayIdx = Integer.parseInt(row.getCell(cellIndex++).getStringCellValue());

        return new ScenarioParameters(priceBattery, priceHWAddOn, socStart, powerChargeMax, dayIdx);
    }

    private static Double parseSumCost(Row row) {
        int lastCellIndex = row.getLastCellNum() - 1;  // The sumCost is in the last cell of the row
        return Double.parseDouble(row.getCell(lastCellIndex).getStringCellValue());
    }

}
