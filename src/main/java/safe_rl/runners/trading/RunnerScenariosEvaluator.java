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
import safe_rl.other.CapacityOptimizer;
import safe_rl.persistance.ElDataHelper;

import java.util.Set;

import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsG2V;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.persistance.ElDataFinals.SOC_TERMINAL_MIN;
import static safe_rl.runners.trading.RunnerHelperTrading.trainerSimulatorPairNight;

public class RunnerScenariosEvaluator {

    public static final int NOF_EPISODES_G2V = 1_000;
    public static final int NOF_EPISODES_V2G = 500;
    public static final double POWER_CHARGE_MAX1 = 22d;
    public static final double PRICE_BATTERY1 = 30e3;
    public static final int SCEN_NAME_COL = 0;
    public static final int  HEADER_ROW=0;
    public static final double CAP_COST_HW = 0.5;
    public static final int ROW_KEY_G2V = 1;
    public static final int ROW_KEY_V2G = 2;

    public static int DAY_IDX = 2;


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

        double valG2V = simulator.valueInStartState();
        resTable.put(ROW_KEY_G2V, SCEN_NAME_COL, "G2V");
        putDataInRow(resTable, ROW_KEY_G2V, "G2V", Triple.of(valG2V, -0d,0d));

        resTable.put(ROW_KEY_V2G, SCEN_NAME_COL, "V2G");
        settings = getSettingsV2G(
                energyFcrPricePair, DUMMY_CAP, SOC_TERMINAL_MIN, POWER_CHARGE_MAX, PRICE_BATTERY);
        var optimizer = new CapacityOptimizer(settings, TOL_GOLDEN_SEARCH, NOF_EPISODES_V2G);
        var capBest = optimizer.optimize();
        putDataInRow(resTable, ROW_KEY_V2G,"V2G", Triple.of(capBest.getSecond(), -CAP_COST_HW,0d));

        System.out.println("resTable = " + resTable);

        computeSumColumn(resTable,Set.of(1,2),Set.of(1,2,3),4);
        printTableAsMatrix(resTable);
    }

    private static void plotting(SettingsTrading settings, Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator) throws JOptimizerException {
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS).settings(settings)
                .socStart(SOC_START)
                .build();
        helper.plotAndPrint(trainerAndSimulator, new CpuTimer(), "");
    }


    private static void createHeader(Table<Integer, Integer, String> resTable) {
        resTable.put(HEADER_ROW, SCEN_NAME_COL, "Scenario");
        resTable.put(HEADER_ROW, 1, "Trading rev.");
        resTable.put(HEADER_ROW, 2, "HW cost");
        resTable.put(HEADER_ROW, 3, "House el cost");
        resTable.put(HEADER_ROW, 4, "Adj. rev.");
    }

    private static void putDataInRow(Table<Integer, Integer, String> resTable, int rowIdx,String scenName, Triple<Double,Double,Double> data) {
        resTable.put(ROW_KEY_G2V, 0, scenName);
        resTable.put(rowIdx, 1, formatter.format(data.getLeft()));
        resTable.put(rowIdx, 2, formatter.format(data.getMiddle()));
        resTable.put(rowIdx, 3, formatter.format(data.getRight()));
    }

    public static void computeSumColumn(Table<Integer, Integer, String> table, Set<Integer> rowKeys, Set<Integer> columnKeys, int sumColumnKey) {

        for (Integer rowKey : rowKeys) {
            double sum = HEADER_ROW;
            for (Integer columnKey : columnKeys) {
                String value = table.get(rowKey, columnKey);
                if (value != null) {
                    double valueNum = Double.parseDouble(value);
                    sum += valueNum;
                }
            }
            table.put(rowKey, sumColumnKey, Double.toString(sum)); // Put the sum in the specified sum column
        }
    }

    public static <R, C, V> void printTableAsMatrix(Table<R, C, V> table) {
        // Get the row and column keys
        Set<R> rowKeys = table.rowKeySet();
        Set<C> columnKeys = table.columnKeySet();

        // Calculate the maximum length of entries in each column
        int[] maxLengths = new int[columnKeys.size()];
        int i = HEADER_ROW;
        for (C columnKey : columnKeys) {
            maxLengths[i] = columnKey.toString().length();
            for (R rowKey : rowKeys) {
                V value = table.get(rowKey, columnKey);
                if (value != null) {
                    maxLengths[i] = Math.max(maxLengths[i], value.toString().length());
                }
            }
            i++;
        }

        // Print the header row
        System.out.print(String.format("%-8s", "")); // Initial space for the row headers
        i = HEADER_ROW;
        for (C columnKey : columnKeys) {
            System.out.print(String.format("%-" + (maxLengths[i] + 4) + "s", columnKey));
            i++;
        }
        System.out.println();

        // Print the rows
        for (R rowKey : rowKeys) {
            System.out.print(String.format("%-8s", rowKey)); // Print the row header
            i = HEADER_ROW;
            for (C columnKey : columnKeys) {
                V value = table.get(rowKey, columnKey);
                System.out.print(String.format("%-" + (maxLengths[i] + 4) + "s", (value != null ? value : "")));
                i++;
            }
            System.out.println();
        }
    }

}
