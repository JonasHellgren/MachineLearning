package safe_rl.runners.trading;

import com.joptimizer.exception.JOptimizerException;
import common.other.CpuTimer;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.simulator.AgentSimulator;
import safe_rl.domain.trainer.TrainerMultiStepACDC;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.persistance.ElDataHelper;

import static safe_rl.environments.factories.SettingsTradingFactory.getSettingsG2V;
import static safe_rl.persistance.ElDataFinals.*;
import static safe_rl.persistance.ElDataFinals.SOC_TERMINAL_MIN;
import static safe_rl.runners.trading.RunnerHelperTrading.trainerSimulatorPairFewEpis;

public class RunnerScenariosEvaluator {

    public static int DAY_IDX = 2;


    @SneakyThrows
    public static void main(String[] args) {
        var dayId = DAYS.get(DAY_IDX);
        var energyFcrPricePair = ElDataHelper.getPricePair(dayId, FROM_TO_HOUR, Pair.create(FILE_ENERGY, FILE_FCR));
        SettingsTrading settings = getSettingsG2V(energyFcrPricePair, SOC_TERMINAL_MIN, 22d, 30e3);
        System.out.println("settings.powerAvgFcrExtreme(0.5) = " + settings.powerAvgFcrExtreme(0.5));
        System.out.println("settings.minAbsolutePowerCharge() = " + settings.minAbsolutePowerCharge());
        settings.check();

        var trainerAndSimulator = trainerSimulatorPairFewEpis(settings, DYMMY_N_SIMULATIONS, SOC_START, 300);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        trainer.train();
        plotting(settings, trainerAndSimulator);


    }

    private static void plotting(SettingsTrading settings, Pair<TrainerMultiStepACDC<VariablesTrading>, AgentSimulator<VariablesTrading>> trainerAndSimulator) throws JOptimizerException {
        var helper = RunnerHelperTrading.<VariablesTrading>builder()
                .nSim(N_SIMULATIONS).settings(settings)
                .socStart(SOC_START)
                .build();
        helper.plotAndPrint(trainerAndSimulator, new CpuTimer(), "");
    }


}
