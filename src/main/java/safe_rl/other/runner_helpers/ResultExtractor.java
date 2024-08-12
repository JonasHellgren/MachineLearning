package safe_rl.other.runner_helpers;

import com.joptimizer.exception.JOptimizerException;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.capacity_search.CapacityOptimizer;

import static safe_rl.persistance.ElDataFinals.TOL_GOLDEN_SEARCH;

public class ResultExtractor {

    public static final int NOF_EPISODES_G2V = 1_000;
    public static final int NOF_EPISODES_V2G = 500;

    public static Pair<Double, Double> getResultV2G(SettingsTrading settings) {
        settings.check();
        var optimizer=new CapacityOptimizer(settings,  TOL_GOLDEN_SEARCH,NOF_EPISODES_V2G);
        return optimizer.optimize();
    }

    public static Pair<Double,StateI<VariablesTrading>> getResultG2V(SettingsTrading settings) throws JOptimizerException {
        settings.check();
        var trainerAndSimulator = TrainerSimulatorFactoryTrading.trainerSimulatorPairNight(settings, NOF_EPISODES_G2V);
        var trainer = trainerAndSimulator.getFirst();
        var simulator = trainerAndSimulator.getSecond();
        trainer.train();
        return Pair.create(simulator.sumRewardsFromSimulations(1),simulator.endStateFromSingleSimulation());
    }

}
