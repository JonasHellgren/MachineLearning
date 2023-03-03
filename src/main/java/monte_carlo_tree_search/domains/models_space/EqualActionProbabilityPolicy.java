package monte_carlo_tree_search.domains.models_space;

import common.RandUtils;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EqualActionProbabilityPolicy implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {
    Set<ShipActionSet> applicableActions;

    public EqualActionProbabilityPolicy(Set<ShipActionSet> applicableActions) {
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
