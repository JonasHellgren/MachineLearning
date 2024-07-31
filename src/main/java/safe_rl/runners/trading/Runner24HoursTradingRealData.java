package safe_rl.runners.trading;

import com.joptimizer.exception.JOptimizerException;
import common.list_arrays.ListUtils;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.safety_layer.SafetyLayer;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.FactoryOptModel;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.EnvironmentTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.persistance.trade_environment.*;

import java.util.List;

public class Runner24HoursTradingRealData {

    static final String PATH = "src/main/java/safe_rl/persistance/data/";
    public static final ElPriceRepoPlotter.Settings NO_LEGEND_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault().withIsLegend(false);
    public static final ElPriceRepoPlotter.Settings DEF_SETTINGS =
            ElPriceRepoPlotter.Settings.newDefault();
    public static final int N_CLUSTERS = 3;
    public static final Pair<Integer, Integer> FROM_TO_HOUR = Pair.create(17, 8);
    public static final double SOC_TERMINAL_MIN = 0.8;
    public static final int POWER_CAPACITY_FCR = 10;

    static PathAndFile fileEnergy = PathAndFile.xlsxOf(PATH, "day-ahead-2024_EurPerMWh");
    static PathAndFile fileFcr = PathAndFile.xlsxOf(PATH, "fcr-n-2024_EurPerMW");


    public static int DAY_IDX = 0;
    public static List<DayId> DAYS = List.of(
            DayId.of(24,0,0,"se3"),
            DayId.of(24,0,4,"se3")
    );
    public static final int N_SIMULATIONS = 5;
    public static final double SOC_START = 0.5;

    @SneakyThrows
    public static void main(String[] args) {
        var dayId=DAYS.get(DAY_IDX);
        ElPriceRepo repo = ElPriceRepo.empty();
        ElPriceXlsReader reader = ElPriceXlsReader.of(repo);
        reader.readDataFromFile(fileEnergy, ElType.ENERGY);
        reader.readDataFromFile(fileFcr, ElType.FCR);
        var elDataEnergyEuroPerMwh=repo.pricesFromHourToHour(dayId, FROM_TO_HOUR,ElType.ENERGY);
        var elDataFcrEuroPerMW=repo.pricesFromHourToHour(dayId, FROM_TO_HOUR,ElType.FCR);
        var elDataEnergyEuroPerKwh=repo.fromPerMegaToPerKilo(elDataEnergyEuroPerMwh);
        var elDataFCREuroPerKW=repo.fromPerMegaToPerKilo(elDataFcrEuroPerMW);
        var settings =SettingsTradingFactory.new100kWhVehicleEmptyPrices()
                .withPowerCapacityFcr(POWER_CAPACITY_FCR)
                .withEnergyPriceTraj(ListUtils.toArray(elDataEnergyEuroPerKwh))
                .withCapacityPriceTraj(ListUtils.toArray(elDataFCREuroPerKW))
                .withSocTerminalMin(SOC_TERMINAL_MIN);
        settings.check();
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS).settings(settings)
                .socStart(SOC_START)
                .build();
        var trainerAndSimulator = helper.createTrainerAndSimulator();
        var trainer = trainerAndSimulator.getFirst();
        trainer.train();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);
        helper.plotAndPrint(trainerAndSimulator,timer,dayId.toDateString());
    }
}
