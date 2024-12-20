package safe_rl.runners.trading;

import com.google.common.collect.Range;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import safe_rl.environments.factories.AgentParametersFactory;
import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.factories.TrainerParametersFactory;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.runner_helpers.PlotterSaverAndPrinterTrading;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;

import java.util.List;

public class Runner24HoursTrading {
    public static final double PRICE_BATTERY = 30e3;
    public static final double STD_ACTIVATION_FCR = 0.1;
    public static final int NOF_EPISODES = 3000;

    public static int CASE_NR = 2;
    public static List<String> CASES = List.of("zeroCap90Tar", "30Cap50Tar", "zigZaw");
    public static final List<Double> POWER_CAPACITY_FCR_LIST = List.of(0d, 30.0, 10.0);
    public static final List<Double> SOC_INCREASE_LIST = List.of(0.4, 0.0, 0.0);
    public static final int N_SIMULATIONS = 5;
    public static final double SOC_START = 0.5;

    @SneakyThrows
    public static void main(String[] args) {
        Double powerCapacityFCR = POWER_CAPACITY_FCR_LIST.get(CASE_NR);
        var settings = CASE_NR == 2
                ? SettingsTradingFactory.new24HoursZigSawPrice()  //case 2
                .withPowerCapacityFcrRange(Range.closed(0d,powerCapacityFCR))
                .withStdActivationFCR(STD_ACTIVATION_FCR)
                .withSocStart(SOC_START)
                .withSocDelta(SOC_INCREASE_LIST.get(CASE_NR))
                .withPriceBattery(PRICE_BATTERY)
                : SettingsTradingFactory.new24HoursIncreasingPrice()  //case 0 and 1
                .withPowerCapacityFcrRange(Range.closed(0d,powerCapacityFCR))
                .withStdActivationFCR(STD_ACTIVATION_FCR)
                .withSocStart(SOC_START)
                .withSocDelta(SOC_INCREASE_LIST.get(CASE_NR))
                .withPriceBattery(PRICE_BATTERY);

        var trainerAndSimulator = TrainerSimulatorFactoryTrading.<VariablesTrading>createTrainerAndSimulator(
                TrainerParametersFactory.trading24Hours().withNofEpisodes(NOF_EPISODES),
                AgentParametersFactory.trading24Hours(settings), settings);
        var trainer = trainerAndSimulator.getFirst();
        trainer.train();
        var timer = CpuTimer.newWithTimeBudgetInMilliSec(0);
        var helper = PlotterSaverAndPrinterTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS).settings(settings)
                .build();
        helper.plotAndPrint(trainerAndSimulator,timer,CASES.get(CASE_NR));
    }

}
