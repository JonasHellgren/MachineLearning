package monte_carlo_tree_search.domains.energy_trading;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.create_tree.MonteCarloSearchStatistics;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

@Log
@Getter
public class EnergyTradingRunner {

    MonteCarloTreeCreator<VariablesEnergyTrading, Integer> mcForSearch;
    EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;

    public EnergyTradingRunner(MonteCarloTreeCreator<VariablesEnergyTrading, Integer> mcForSearch,
                               EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment) {
        this.mcForSearch = mcForSearch;
        this.environment = environment;
    }

    @SneakyThrows
    public void run(StateInterface<VariablesEnergyTrading> state) throws StartStateIsTrapException {
        int i = 0;
        StepReturnGeneric<VariablesEnergyTrading> sr;
        do {
            mcForSearch.setStartState(state);
            mcForSearch.run();

            MonteCarloSearchStatistics<VariablesEnergyTrading, Integer> stats=mcForSearch.getStatistics();
          //  SimulationPolicyInterface<VariablesEnergyTrading, Integer> policy=settings.getSimulationPolicy();
           // PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping policyCasted=(PolicyMoveUpAndDownStopEveryFloorRandomDirectionAfterStopping) policy;
          //  Set<Integer> actions=policyCasted.availableActionValues(state);
            log.info("Search completed, tree size = " +stats.getNofNodes()+", tree depth = "+stats.getMaxDepth()+", nof iter = "+stats.getNofIterations()); //+", actions = "+actions);

            sr = environment.step(mcForSearch.getFirstAction(), state);
            state.setFromReturn(sr);
            //double rootNodeValue= ListUtils.findEnd(mcForSearch.getPlotData()).orElse(TreePlotData.builder().build()).maxValue;
            i++;
        } while (!sr.isTerminal && !sr.isFail);

    }


}
