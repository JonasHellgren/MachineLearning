package safe_rl.runners.trading;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import safe_rl.persistance.ElDataHelper;
import safe_rl.persistance.trade_environment.PathAndFile;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsG2V;
import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsV2G;
import static safe_rl.other.runner_helpers.ResultExtractor.getResultG2V;
import static safe_rl.other.runner_helpers.ResultExtractor.getResultV2G;
import static safe_rl.other.scenerio_table.ScenarioTable2ExcelConverter.convertTableToExcel;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.ROW_KEY_G2V;
import static safe_rl.other.scenerio_table.ScenarioTableHelper.*;
import static safe_rl.persistance.ElDataFinals.*;

public class RunnerSingleScenario {

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

        computeSumColumns(resTable, ROWS_SCEANRIOS, COLUMNS_DATA,SUM_COLUMN);
        printTableAsMatrix(resTable);
        convertTableToExcel(resTable, PathAndFile.xlsxOf(RES_PATH,"scen_res_"+dayId.toString()));
    }





}
