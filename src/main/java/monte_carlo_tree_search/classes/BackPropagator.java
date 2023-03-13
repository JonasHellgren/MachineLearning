package monte_carlo_tree_search.classes;

import common.CpuTimer;
import lombok.Builder;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.MemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.List;
import java.util.Optional;

@Log
@Builder
public class BackPropagator<S, A> {

    EnvironmentGenericInterface<S, A> environment;
    StateInterface<S> startState;
    MonteCarloSettings<S, A> settings;
    NodeWithChildrenInterface<S, A> nodeRoot;
    List<ActionInterface<A>> actionsToSelected;
    MemoryInterface<S> memory;


    void chooseTestedActionAndBackPropagate(NodeWithChildrenInterface<S, A> nodeSelected, ActionSelector<S, A> actionSelector) {
        log.fine("The selected node has either all children as fails or is at max depth");
        StateInterface<S> state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        Optional<ActionInterface<A>> actionInSelected = actionSelector.selectBestTestedAction(nodeSelected);
        StepReturnGeneric<S> sr = environment.step(actionInSelected.orElseThrow(), state);
        SimulationResults simulationResults = SimulationResults.newEmpty();
        backPropagate(sr, simulationResults, actionInSelected.orElseThrow());
    }

    void backPropagate(StepReturnGeneric<S> sr,
                       SimulationResults simulationResults,
                       ActionInterface<A> actionInSelected) {
        SimulationReturnsExtractor<S, A> bumSim = SimulationReturnsExtractor.<S, A>builder()
                .nofNodesOnPath(actionsToSelected.size() + 1)
                .simulationResults(simulationResults)
                .settings(settings)
                .build();


        BackupModifier<S, A> bum = BackupModifier.<S, A>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .settings(settings)
                .returnsSimulation(bumSim.getSimulationReturns())
                .memoryValueStateAfterAction(memory.read(sr.newState))
                .build();
        bum.backup();
    }


}
