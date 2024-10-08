package safe_rl.runners.trading;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import safe_rl.other.scenario_creator.ScenarioParameterVariantsFactory;
import safe_rl.other.scenario_creator.ScenarioParameters;
import safe_rl.other.scenario_creator.ScenariosCreator;
import safe_rl.persistance.ElDataHelper;

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

        for (ScenarioParameters p : creator.scenarios()) {
            var dayId = DAYS_CLUSTER_ANALYSIS.get(p.dayIdx());
            double costHwPerDay = getCostHwPerDay(p.priceHWAddOn());

            Table<Integer, Integer, String> resTable = HashBasedTable.create();
            createHeader(resTable);

            var energyFcrPricePair = ElDataHelper.getPricePair(dayId, FROM_TO_HOUR, Pair.create(FILE_ENERGY, FILE_FCR));
            var settings = getSettingsG2V(energyFcrPricePair, p.socStart(), SOC_DELTA, p.powerChargeMax(), p.priceBattery());
            double valG2V = getResultG2V(settings);
            putDataInRow(resTable, ROW_KEY_G2V, "G2V", Triple.of(valG2V, -0d, 0d));

            settings = getSettingsV2G(
                    energyFcrPricePair, DUMMY_CAP_NON_ZERO,p.socStart(), SOC_DELTA, p.powerChargeMax(), p.priceBattery());
            var resultV2G = getResultV2G(settings);
            putDataInRow(resTable, ROW_KEY_V2G, "V2G", Triple.of(resultV2G.getSecond(), -costHwPerDay, 0d));

            computeSumColumns(resTable, ROWS_SCEANRIOS, COLUMNS_DATA, SUM_COLUMN);
            printTableAsMatrix(resTable);
        }

    }

}
