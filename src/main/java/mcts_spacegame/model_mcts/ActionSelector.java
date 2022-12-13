package mcts_spacegame.model_mcts;

import common.RandUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
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
    RandUtils<Action> randUtils;

    public ActionSelector(MonteCarloSettings settings) {
        this.randUtils=new RandUtils<>();
        this.settings=settings;
    }

    public Optional<Action> select(NodeInterface nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<Action> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActions(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }

    private Action getActionFromPolicy(NodeInterface nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private Action getRandomAction(List<Action> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private Action getRandomTestedAction(NodeInterface nodeSelected) {
        log.warning("No non-tested actions");
        List<Action> testedActions = getTestedActions(nodeSelected);
        int nofTestedActions=testedActions.size();
        if (nofTestedActions==0) {
            throw new RuntimeException("nofTestedActions=0");
        }
        return getRandomAction(testedActions);
    }

    @NotNull
    private List<Action> getNonTestedActions(NodeInterface nodeSelected) {
        List<Action> testedActions = getTestedActions(nodeSelected);
        List<Action> nonTestedActions=new ArrayList<>(Action.applicableActions());  //must be mutable
        nonTestedActions.removeAll(testedActions);
        return nonTestedActions;
    }

    @NotNull
    private List<Action> getTestedActions(NodeInterface nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
