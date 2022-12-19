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
public class ActionSelector<SSV,AV> {
    MonteCarloSettings settings;
    RandUtils<ActionInterface<AV>> randUtils;

    public ActionSelector(MonteCarloSettings settings) {
        this.randUtils=new RandUtils<>();
        this.settings=settings;
    }

    public Optional<ActionInterface<AV>> select(NodeInterface<SSV,AV> nodeSelected) {
        int nofTestedActions=getTestedActions(nodeSelected).size();

        List<ActionInterface<AV>> nonTestedActions = (nofTestedActions==0)
                ? Collections.singletonList(getActionFromPolicy(nodeSelected))
                : getNonTestedActions(nodeSelected);

        if(nonTestedActions.size()==0) {
            return Optional.empty();
        } else {
            return Optional.of(getRandomAction(nonTestedActions));
        }
    }

    private ActionInterface<AV> getActionFromPolicy(NodeInterface<SSV,AV> nodeSelected) {
        return settings.firstActionSelectionPolicy.chooseAction(nodeSelected.getState());
    }

    private ActionInterface<AV> getRandomAction(List<ActionInterface<AV>> actions) {
        return randUtils.getRandomItemFromList(actions);
    }

    private ActionInterface<AV> getRandomTestedAction(NodeInterface<SSV,AV> nodeSelected) {
        log.warning("No non-tested actions");
        List<ActionInterface<AV>> testedActions = getTestedActions(nodeSelected);
        int nofTestedActions=testedActions.size();
        if (nofTestedActions==0) {
            throw new RuntimeException("nofTestedActions=0");
        }
        return getRandomAction(testedActions);
    }

    @NotNull
    private List<ActionInterface<AV>> getNonTestedActions(NodeInterface<SSV,AV> nodeSelected) {
        List<ActionInterface<AV>> testedActions = getTestedActions(nodeSelected);
        List<AV> testedActionValues=testedActions.stream().map(ActionInterface::getValue).collect(Collectors.toList());

        Set<AV> allValues=nodeSelected.getAction().applicableActions();
        List<AV> nonTestedActionValues=ActionInterface.getNonTestedActionValues(testedActionValues,allValues);

        List<ActionInterface<AV>> nonTestedActions=new ArrayList<>();

        for (AV value:nonTestedActionValues) {
            nonTestedActions.add(ActionInterface.newAction(value));  //todo base on Interface
        }

        return nonTestedActions;
    }

    @NotNull
    private List<ActionInterface<AV>> getTestedActions(NodeInterface<SSV,AV> nodeSelected) {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
