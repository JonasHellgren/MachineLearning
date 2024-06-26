package monte_carlo_tree_search.domains.energy_trading;

import common.list_arrays.ListUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.create_tree.MonteCarloSearchStatistics;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;

@Log
@Getter
public class EnergyTradingRunner {

    private static final String NEW_LINE = System.lineSeparator();
    MonteCarloTreeCreator<VariablesEnergyTrading, Integer> mcForSearch;
    EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment;
    List<Integer> actions;
    List<Integer> times;
    List<Double> SoElist;
    List<Double> rewards;


    public EnergyTradingRunner(MonteCarloTreeCreator<VariablesEnergyTrading, Integer> mcForSearch,
                               EnvironmentGenericInterface<VariablesEnergyTrading, Integer> environment) {
        this.mcForSearch = mcForSearch;
        this.environment = environment;
        this.actions = new ArrayList<>();
        this.SoElist = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.times=new ArrayList<>();
    }

    public void run(StateInterface<VariablesEnergyTrading> state) {
        int i = 0;
        StepReturnGeneric<VariablesEnergyTrading> sr;
        do {

            mcForSearch.setStartState(state.copy());
            try {
                mcForSearch.run();
            } catch (StartStateIsTrapException e) {
                log.warning("State is trap = "+state);
                sr = environment.step(mcForSearch.getFirstAction(), state);
                setLists(state, mcForSearch.getFirstAction().getValue(), sr.reward);
                break;
            }
            MonteCarloSearchStatistics<VariablesEnergyTrading, Integer> stats = mcForSearch.getStatistics();
            log.info("Search completed, tree size = " + stats.getNofNodes() + ", tree depth = " + stats.getMaxDepth() + ", nof iter = " + stats.getNofIterations()); //+", actions = "+actions);

            sr = environment.step(mcForSearch.getFirstAction(), state);
            setLists(state,mcForSearch.getFirstAction().getValue(), sr.reward);
            state.setFromReturn(sr);
            //double rootNodeValue= ListUtils.findEnd(mcForSearch.getPlotData()).orElse(TreePlotData.builder().build()).maxValue;
            i++;
        } while (!sr.isTerminal && !sr.isFail);
        setLists(state, 0,0);
    }

    private void setLists(StateInterface<VariablesEnergyTrading> state, int action, double reward) {
        SoElist.add(state.getVariables().SoE);
        times.add(state.getVariables().time);
        actions.add(action);
        rewards.add(reward);   //sr.isTerminal?0:
    }

    public String toString() {

        return          "actions = " + actions.toString() + NEW_LINE +
                        "times = " + times.toString() + NEW_LINE +
                        "SoElist = "+ SoElist.toString() + NEW_LINE +
                        "rewards = "+rewards.toString() + NEW_LINE +
                        "sumRewardsNSteps = "+ListUtils.sumDoubleList(rewards);

    }


}
