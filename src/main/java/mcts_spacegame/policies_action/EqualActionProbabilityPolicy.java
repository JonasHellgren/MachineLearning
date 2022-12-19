package mcts_spacegame.policies_action;

import common.RandUtils;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

public class EqualActionProbabilityPolicy implements SimulationPolicyInterface {
    @Override
    public ShipAction chooseAction(StateInterface<ShipVariables> state) {
        RandUtils<ShipAction> randUtils=new RandUtils<>();
        return randUtils.getRandomItemFromList(ShipAction.applicableActions());
     }
}
