package mcts_spacegame.models_space;

import common.RandUtils;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.SimulationPolicyInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

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
