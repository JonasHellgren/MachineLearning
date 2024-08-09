package safe_rl.runners.trading;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.capacity_search.CapacityOptimizer;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.PathAndFile;

import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsG2V;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.other.scenerio_table.ScenarioTable2ExcelConverter.convertTableToExcel;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.ROW_KEY_G2V;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.*;
import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.persistance.ElDataFinals.SOC_TERMINAL_MIN;
import static safe_rl.runners.trading.RunnerHelperTrading.trainerSimulatorPairNight;

public class RunnerScenariosEvaluator {

    public static final int NOF_EPISODES_G2V = 1_000;
    public static final int NOF_EPISODES_V2G = 500;
    public static final double POWER_CHARGE_MAX1 = 22d;
    public static final double PRICE_BATTERY1 = 30e3;
    public static final double CAP_COST_HW = 0.5;
    public static int DAY_IDX = 0;


    @SneakyThrows
    public static void main(String[] args) {
        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair = ElDataHelper.getPricePair(dayId, FROM_TO_HOUR, Pair.create(FILE_ENERGY, FILE_FCR));
        Table<Integer, Integer, String> resTable = HashBasedTable.create();
        createHeader(resTable);

        var settings = getSettingsG2V(energyFcrPricePair, SOC_TERMINAL_MIN, POWER_CHARGE_MAX1, PRICE_BATTERY1);
        settings.check();
        var trainerAndSimulator = trainerSimulatorPairNight(settings, DYMMY_N_SIMULATIONS, SOC_START, NOF_EPISODES_G2V);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        trainer.train();
        plotting(settings, trainerAndSimulator);

        double valG2V = simulator.simulationValueInStartState(1);
        putDataInRow(resTable, ROW_KEY_G2V, "G2V", Triple.of(valG2V, -0d,0d));

        settings = getSettingsV2G(
                energyFcrPricePair, DUMMY_CAP_NON_ZERO, SOC_TERMINAL_MIN, POWER_CHARGE_MAX, PRICE_BATTERY);
        settings.check();
        var optimizer = new CapacityOptimizer(settings, TOL_GOLDEN_SEARCH, NOF_EPISODES_V2G);
        var capBest = optimizer.optimize();
        putDataInRow(resTable, ROW_KEY_V2G,"V2G", Triple.of(capBest.getSecond(), -CAP_COST_HW,0d));

        computeSumColumns(resTable, ROWS_SCEANRIOS, COLUMNS_DATA,4);
        printTableAsMatrix(resTable);
        convertTableToExcel(resTable, PathAndFile.xlsxOf(RES_PATH,"scen_res_"+dayId.toString()));
    }

    private static void plotting(SettingsTrading settings, Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator) throws JOptimizerException {
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS_PLOTTING).settings(settings)
                .socStart(SOC_START)
                .build();
        helper.plotAndPrint(trainerAndSimulator, new CpuTimer(), "");
    }




}
