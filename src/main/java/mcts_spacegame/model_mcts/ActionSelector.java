package mcts_spacegame.model_mcts;

import common.RandUtils;
import lombok.extern.java.Log;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is for determining the action to choose in the selected node, the action that will lead to tree
 * expansion.
 */

@Log
public class ActionSelector {
    MonteCarloSettings settings;
    RandUtils<ActionInterface<ShipActionSet>> randUtils;

    public ActionSelector(MonteCarloSettings settings) {
        this.randUtils=new RandUtils<>();
        this.settings=settings;
    }

    public Optional<ActionInterface<ShipActionSet>> select(NodeInterface nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<ActionInterface<ShipActionSet>> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActions(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }

    private ActionInterface<ShipActionSet> getActionFromPolicy(NodeInterface nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private ActionInterface<ShipActionSet> getRandomAction(List<ActionInterface<ShipActionSet>> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private ActionInterface<ShipActionSet> getRandomTestedAction(NodeInterface nodeSelected) {
        log.warning("No non-tested actions");
        List<ActionInterface<ShipActionSet>> testedActions = getTestedActions(nodeSelected);
        int nofTestedActions=testedActions.size();
        if (nofTestedActions==0) {
            throw new RuntimeException("nofTestedActions=0");
        }
        return getRandomAction(testedActions);
    }

    @NotNull
    private List<ActionInterface<ShipActionSet>> getNonTestedActions(NodeInterface nodeSelected) {
        List<ActionInterface<ShipActionSet>> testedActions = getTestedActions(nodeSelected);
        List<ShipActionSet> testedActionValues=testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());
        List<ShipActionSet> nonTestedActionValues=ShipActionSet.getNonTestedActionValues(testedActionValues);

        List<ActionInterface<ShipActionSet>> nonTestedActions=new ArrayList<>();

        for (ShipActionSet value:nonTestedActionValues) {
            nonTestedActions.add(new ActionShip(value));  //todo base on Interface
        }

        return nonTestedActions;
    }

    @NotNull
    private List<ActionInterface<ShipActionSet>> getTestedActions(NodeInterface nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
