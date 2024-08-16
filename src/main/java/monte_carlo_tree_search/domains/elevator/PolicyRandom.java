package monte_carlo_tree_search.domains.elevator;

import common.other.RandUtilsML;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PolicyRandom
        implements SimulationPolicyInterface<VariablesElevator, Integer> {

    RandUtilsML<Integer> randGenerator;
    List<Integer> actionList;

    public PolicyRandom() {
        randGenerator=new RandUtilsML<>();
        ActionElevator actionElevator=ActionElevator.newValueDefaultRange(0);
        actionList= new ArrayList<>(actionElevator.applicableActions());
    }

    @Override
    public ActionInterface<Integer> chooseAction(StateInterface<VariablesElevator> state) {

        int actionValue= randGenerator.getRandomItemFromList(actionList);
        return ActionElevator.newValueDefaultRange(actionValue);
    }

    @Override
    public Set<Integer> availableActionValues(StateInterface<VariablesElevator> state) {
        ActionElevator actionElevator=ActionElevator.newValueDefaultRange(0);
        return actionElevator.applicableActions();
    }
}
