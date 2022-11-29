package mcts_spacegame.model_mcts;

import common.ConditionalUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class ActionSelector {

    NodeInterface nodeSelected;

    public ActionSelector(NodeInterface nodeSelected) {
        this.nodeSelected = nodeSelected;
    }

    public Action select() {

        List<Action> nonTestedActions = getNonTestedActions();
        int nofNonTestedActions=nonTestedActions.size();
        ConditionalUtils.executeOnlyIfConditionIsTrue(nofNonTestedActions==0,
                () -> log.warning("No non tested actions"));
        return nonTestedActions.get(RandomUtils.nextInt(0,nofNonTestedActions));
    }

    @NotNull
    private List<Action> getNonTestedActions() {
        List<Action> testedActions = nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
        List<Action> nonTestedActions=new ArrayList<>(Action.applicableActions());  //must be mutable
        nonTestedActions.removeAll(testedActions);
        return nonTestedActions;
    }

}
