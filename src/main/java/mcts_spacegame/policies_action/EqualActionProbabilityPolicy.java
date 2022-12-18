package mcts_spacegame.policies_action;

import common.RandUtils;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.State;

public class EqualActionProbabilityPolicy implements SimulationPolicyInterface {
    @Override
    public ShipAction chooseAction(State state) {
        RandUtils<ShipAction> randUtils=new RandUtils<>();
        return randUtils.getRandomItemFromList(ShipAction.applicableActions());
     }
}
