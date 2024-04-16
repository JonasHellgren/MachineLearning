package monte_carlo_tree_search.domains.models_space;

import common.other.RandUtils;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PolicyEqualActionProbability implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {
    Set<ShipActionSet> applicableActions;

    public PolicyEqualActionProbability(Set<ShipActionSet> applicableActions) {
        this.applicableActions = applicableActions;
    }

    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        RandUtils<ShipActionSet> randUtils=new RandUtils<>();
        List<ShipActionSet> avList= new ArrayList<>(applicableActions);
        return new ActionShip(randUtils.getRandomItemFromList(avList));
     }

    @Override
    public Set<ShipActionSet> availableActionValues(StateInterface<ShipVariables> state) {
        throw new RuntimeException("Not implemented");
    }

}
