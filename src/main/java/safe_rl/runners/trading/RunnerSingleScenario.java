package safe_rl.runners.trading;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joptimizer.exception.JOptimizerException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.other.capacity_search.CapacityOptimizer;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.PathAndFile;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsG2V;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.other.scenerio_table.ScenarioTable2ExcelConverter.convertTableToExcel;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.ROW_KEY_G2V;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.*;
import static safe_rl.persistance.ElDataFinals.*;

public class RunnerSingleScenario {

    public static final int NOF_EPISODES_G2V = 1_000;
    public static final int NOF_EPISODES_V2G = 500;

    public static int DAY_IDX = 1;

    @SneakyThrows
    public static void main(String[] args) {
        var dayId = DAYS_CLUSTER_ANALYSIS.get(DAY_IDX);
        double costHwPerDay = getCostHwPerDay(PRICE_HW);

        var energyFcrPricePair = ElDataHelper.getPricePair(dayId, FROM_TO_HOUR, Pair.create(FILE_ENERGY, FILE_FCR));
        Table<Integer, Integer, String> resTable = HashBasedTable.create();
        createHeader(resTable);

        var settings = getSettingsG2V(energyFcrPricePair, SOC_START, SOC_DELTA, POWER_CHARGE_MAX, PRICE_BATTERY);
        double valG2V = getResultG2V(settings);
        putDataInRow(resTable, ROW_KEY_G2V, "G2V", Triple.of(valG2V, -0d,0d));

        settings = getSettingsV2G(
                energyFcrPricePair, DUMMY_CAP_NON_ZERO, SOC_START, SOC_DELTA, POWER_CHARGE_MAX, PRICE_BATTERY);
        var resultV2G = getResultV2G(settings);
        putDataInRow(resTable, ROW_KEY_V2G,"V2G", Triple.of(resultV2G.getSecond(), -costHwPerDay,0d));

        computeSumColumns(resTable, ROWS_SCEANRIOS, COLUMNS_DATA,4);
        printTableAsMatrix(resTable);
        convertTableToExcel(resTable, PathAndFile.xlsxOf(RES_PATH,"scen_res_"+dayId.toString()));
    }


    private static Pair<Double, Double> getResultV2G(SettingsTrading settings) {
        settings.check();
        var optimizer=new CapacityOptimizer(settings,  TOL_GOLDEN_SEARCH,NOF_EPISODES_V2G);
        return optimizer.optimize();
    }

    private static double getResultG2V(SettingsTrading settings) throws JOptimizerException {
        settings.check();
        var trainerAndSimulator = TrainerSimulatorFactoryTrading.trainerSimulatorPairNight(settings, NOF_EPISODES_G2V);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        trainer.train();
        return simulator.simulationValueInStartState(1);
    }


}
