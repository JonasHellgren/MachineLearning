package safe_rl.other.capacity_search;

import com.joptimizer.exception.JOptimizerException;
import common.math.FunctionWrapperI;
import common.math.GoldenSearcher;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.environment.aggregates.StateI;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

import static safe_rl.persistance.ElDataFinals.*;

public class CapacityOptimizer {
    GoldenSearcher searcher;
    FunctionWrapperI function;

    public CapacityOptimizer(SettingsTrading settingsTrading,
                             Double tol,
                             int nofEpis) {
        this.function=new CapacityFunctionWrapper(settingsTrading,POOR_VALUE, nofEpis);
        var settingsSearch= SearchSettings.builder()
                .xMin(POWER_MIN).xMax(settingsTrading.minAbsolutePowerCharge())
                .tol(tol).nIterMax(N_ITER_MAX_GOLDEN_SEARCH)
                .build();
        this.searcher=new GoldenSearcher(function,settingsSearch);
    }

    public Pair<Double,Double> optimize() {
        return searcher.searchMaxWithFunctionValue();
    }

    public StateI<VariablesTrading> endStateFromSimulation() throws JOptimizerException {
        CapacityFunctionWrapper fcn=(CapacityFunctionWrapper) function;
        return fcn.endStateFromSimulation();
    }

}
