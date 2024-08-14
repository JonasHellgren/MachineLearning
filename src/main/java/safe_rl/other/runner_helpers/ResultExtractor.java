package safe_rl.other.runner_helpers;

import com.joptimizer.exception.JOptimizerException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.environments.factories.TrainerSimulatorFactoryTrading;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.other.capacity_search.CapacityOptimizer;

import static safe_rl.persistance.ElDataFinals.*;

public class ResultExtractor {


    @SneakyThrows
    //Triple = (capacityBest,sumRewards,endState)
    public static Triple<Double, Double,StateI<VariablesTrading>> getResultV2G(SettingsTrading settings) {
        settings.throwExceptionIfNonCorrect();
        var optimizer=new CapacityOptimizer(settings,  TOL_GOLDEN_SEARCH,NOF_EPISODES_V2G);
        Pair<Double,Double> capSumRewPair= optimizer.optimize();
        StateI<VariablesTrading> endState= optimizer.endStateFromSimulation();
        return Triple.of(capSumRewPair.getFirst(),capSumRewPair.getSecond(),endState);
    }

    //Pair = (sumRewards,endState)
    public static Pair<Double,StateI<VariablesTrading>> getResultG2V(SettingsTrading settings) throws JOptimizerException {
        settings.throwExceptionIfNonCorrect();
        var trainerAndSimulator = TrainerSimulatorFactoryTrading.trainerSimulatorPairNight(settings, NOF_EPISODES_G2V);
        var trainer = trainerAndSimulator.getFirst();
        trainer.train();
        var simulator = trainerAndSimulator.getSecond();
        StateI<VariablesTrading> endStateFromSingleSimulation = simulator.endStateFromSingleSimulation();
        return Pair.create(simulator.sumRewardsFromSimulations(1), endStateFromSingleSimulation);
    }

    public static double dSoHInPPM(StateI<VariablesTrading> state) {
        return (1-state.getVariables().soh())*1e6;
    }

}
