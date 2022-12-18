package mcts_spacegame.model_mcts;

import common.RandUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
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
    RandUtils<ShipAction> randUtils;

    public ActionSelector(MonteCarloSettings settings) {
        this.randUtils=new RandUtils<>();
        this.settings=settings;
    }

    public Optional<ShipAction> select(NodeInterface nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<ShipAction> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActions(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }

    private ShipAction getActionFromPolicy(NodeInterface nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private ShipAction getRandomAction(List<ShipAction> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private ShipAction getRandomTestedAction(NodeInterface nodeSelected) {
        log.warning("No non-tested actions");
        List<ShipAction> testedActions = getTestedActions(nodeSelected);
        int nofTestedActions=testedActions.size();
        if (nofTestedActions==0) {
            throw new RuntimeException("nofTestedActions=0");
        }
        return getRandomAction(testedActions);
    }

    @NotNull
    private List<ShipAction> getNonTestedActions(NodeInterface nodeSelected) {
        List<ShipAction> testedActions = getTestedActions(nodeSelected);
        List<ShipAction> nonTestedActions=new ArrayList<>(ShipAction.applicableActions());  //must be mutable
        nonTestedActions.removeAll(testedActions);
        return nonTestedActions;
    }

    @NotNull
    private List<ShipAction> getTestedActions(NodeInterface nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
