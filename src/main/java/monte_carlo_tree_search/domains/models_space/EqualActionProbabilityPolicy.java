package monte_carlo_tree_search.domains.models_space;

import common.RandUtils;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;

public class EqualActionProbabilityPolicy implements SimulationPolicyInterface<ShipVariables, ShipActionSet> {
    @Override
    public ActionInterface<ShipActionSet> chooseAction(StateInterface<ShipVariables> state) {
        RandUtils<ShipActionSet> randUtils=new RandUtils<>();
        List<ShipActionSet> avList= new ArrayList<>(ShipActionSet.applicableActions());
        return new ActionShip(randUtils.getRandomItemFromList(avList));
     }
}
