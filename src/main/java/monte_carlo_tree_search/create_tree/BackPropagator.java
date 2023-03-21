package monte_carlo_tree_search.create_tree;

import lombok.Builder;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.SimulationResults;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.ReadableMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.List;
import java.util.Optional;

/**
 *  This class executes the last step in MCTS, back propagation
 *  Both step return and/or simulation from previous step are used.
 */

@Log
@Builder
public class BackPropagator<S, A> {

    EnvironmentGenericInterface<S, A> environment;
    StateInterface<S> startState;
    MonteCarloSettings<S, A> settings;
    NodeWithChildrenInterface<S, A> nodeRoot;
    List<ActionInterface<A>> actionsToSelected;
    ReadableMemoryInterface<S> memory;


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
