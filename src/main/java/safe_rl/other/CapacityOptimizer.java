package safe_rl.other;

import common.math.FunctionWrapperI;
import common.math.GoldenSearcher;
import safe_rl.environments.trading_electricity.SettingsTrading;

import static safe_rl.persistance.ElDataFinals.*;

public class CapacityOptimizer {
    GoldenSearcher searcher;
    FunctionWrapperI function;

    public CapacityOptimizer(SettingsTrading settingsTrading, Double tol) {
        this.function=new CapacityFunctionWrapper(settingsTrading,POOR_VALUE);
        var settingsSearch= SearchSettings.builder()
                .xMin(POWER_MIN).xMax(settingsTrading.minAbsolutePowerCharge())
                .tol(tol).nIterMax(N_ITER_MAX_GOLDEN_SEARCH)
                .build();
        this.searcher=new GoldenSearcher(function,settingsSearch);
    }

    public double optimize() {
        return searcher.searchMax();
    }

}
