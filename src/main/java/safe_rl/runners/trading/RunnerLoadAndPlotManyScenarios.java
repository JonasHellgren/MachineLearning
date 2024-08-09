package safe_rl.runners.trading;

import safe_rl.other.scenario_creator.ScenarioExcelToMapConverter;
import safe_rl.persistance.trade_environment.PathAndFile;

import static safe_rl.persistance.ElDataFinals.FILE_MANY_SCEN;
import static safe_rl.persistance.ElDataFinals.RES_PATH;

public class RunnerLoadAndPlotManyScenarios {

    public static void main(String[] args) {

        var scenMap=ScenarioExcelToMapConverter.readExcelToMap(PathAndFile.xlsxOf(RES_PATH, FILE_MANY_SCEN));

        scenMap.entrySet().forEach(e -> System.out.println(e));

    }

}
