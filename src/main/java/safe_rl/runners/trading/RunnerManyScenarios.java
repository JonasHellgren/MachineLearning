package safe_rl.runners.trading;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import safe_rl.other.runner_helpers.ResultExtractor;
import safe_rl.other.scenario_creator.ScenarioParameterVariantsFactory;
import safe_rl.other.scenario_creator.ScenarioParameters;
import safe_rl.other.scenario_creator.ScenarioSumCostMap2ExcelConverter;
import safe_rl.other.scenario_creator.ScenariosCreator;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.PathAndFile;
import java.util.HashMap;
import java.util.Map;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsG2V;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.other.runner_helpers.ResultExtractor.getResultG2V;
import static safe_rl.other.runner_helpers.ResultExtractor.getResultV2G;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.*;
import static safe_rl.persistance.ElDataFinals.*;

public class RunnerManyScenarios {


    @SneakyThrows
    public static void main(String[] args) {

        var creator = new ScenariosCreator(ScenarioParameterVariantsFactory.create());
        Map<ScenarioParameters, Double> sumCostMap = new HashMap<>();

        for (ScenarioParameters p : creator.scenarios()) {
            var dayId = DAYS_CLUSTER_ANALYSIS.get(p.dayIdx());
            double costHwPerDay = getCostHwPerDay(p.priceHWAddOn());

            Table<Integer, Integer, String> resTable = HashBasedTable.create();
            createHeader(resTable);

            var energyFcrPricePair = ElDataHelper.getPricePair(dayId, FROM_TO_HOUR, Pair.create(FILE_ENERGY, FILE_FCR));
            var settings = getSettingsG2V(energyFcrPricePair, p.socStart(), SOC_DELTA, p.powerChargeMax(), p.priceBattery());
            var resG2V= getResultG2V(settings);
            double valG2V=resG2V.getFirst();
            double dSoHG2V= ResultExtractor.dSoHInPercentage(resG2V.getSecond());
            putDataInRow(resTable, ROW_KEY_G2V, "G2V", Triple.of(valG2V, -0d, 0d),dSoHG2V);

            settings = getSettingsV2G(
                    energyFcrPricePair, DUMMY_CAP_NON_ZERO,p.socStart(), SOC_DELTA, p.powerChargeMax(), p.priceBattery());
            var resultV2G =  getResultV2G(settings);
            double valV2G=resultV2G.getMiddle();
            double dSoHV2G= ResultExtractor.dSoHInPercentage(resultV2G.getRight());

            putDataInRow(resTable, ROW_KEY_V2G, "V2G", Triple.of(valV2G, -costHwPerDay, 0d),dSoHV2G);

            computeSumColumns(resTable, ROWS_SCEANARIOS, COLUMNS_DATA, SUM_COLUMN);
            printTableAsMatrix(resTable);
            System.out.println("p = " + p);

            sumCostMap.put(p, sumCosts(resTable, ROW_KEY_V2G)- sumCosts(resTable, ROW_KEY_G2V));
        }

        ScenarioSumCostMap2ExcelConverter.convertTableToExcel(sumCostMap, PathAndFile.xlsxOf(RES_PATH, FILE_MANY_SCEN));

    }

    private static double sumCosts(Table<Integer, Integer, String> resTable, int rowKeyV2g) {
        return Double.parseDouble(resTable.get(rowKeyV2g, SUM_COLUMN));
    }

}
