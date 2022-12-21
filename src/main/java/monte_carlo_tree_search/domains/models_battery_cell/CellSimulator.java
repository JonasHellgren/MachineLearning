package monte_carlo_tree_search.domains.models_battery_cell;

import monte_carlo_tree_search.classes.StepReturnGeneric;

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
}
