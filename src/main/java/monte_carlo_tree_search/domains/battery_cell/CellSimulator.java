package monte_carlo_tree_search.domains.battery_cell;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;

import java.util.ArrayList;
import java.util.List;

public class CellSimulator {

    EnvironmentCell environment;

    public CellSimulator(EnvironmentCell environment) {
        this.environment = environment;
    }

    public List<EnvironmentCell.CellResults> simulate(List<Integer> currentTraj, StateCell stateInit, ActionCell action) {
        List<EnvironmentCell.CellResults> resultsList=new ArrayList<>();
        StateCell state=stateInit.copy();
        for (Integer currentLevel: currentTraj) {
            action.setValue(currentLevel);
            StepReturnGeneric<CellVariables> sr=environment.step(action,state);
            state.setFromReturn(sr);
            resultsList.add(environment.getCellResults());

            if (sr.isTerminal) {
                break;
            }
        }
        return resultsList;
    }

    public  List<EnvironmentCell.CellResults> simulateWithPolicy(SimulationPolicyInterface<CellVariables, Integer> policy,
                                                                int nofSteps,
                                                                StateCell state) {
        List<EnvironmentCell.CellResults> resultsList=new ArrayList<>();
        for (int i = 0; i < nofSteps; i++) {
            ActionInterface<Integer> action=policy.chooseAction(state.copy());
            StepReturnGeneric<CellVariables> sr=environment.step(action,state);
            state.setFromReturn(sr);
            EnvironmentCell environmentCasted= (EnvironmentCell) environment;
            resultsList.add(environmentCasted.getCellResults());
            if (sr.isTerminal) {
                break;
            }
        }
        return resultsList;
    }
}
