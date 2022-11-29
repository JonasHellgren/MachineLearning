package mcts_spacegame.model_mcts;

import common.ConditionalUtils;
import common.MathUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.stream.Collectors;

@Log
public class ActionSelector {

    NodeInterface nodeSelected;

    public ActionSelector(NodeInterface nodeSelected) {
        this.nodeSelected = nodeSelected;
    }

    public Action select() {
        List<Action> testedActions=nodeSelected.getChildNodes().stream().map(cn -> cn.getAction()).collect(Collectors.toList());
        List<Action> nonTestedActions=Action.applicableActions();
        nonTestedActions.removeAll(testedActions);

        System.out.println("nodeSelected ActionSelector = " + nodeSelected);
        System.out.println("nonTestedActions ActionSelector = " + nonTestedActions);
        int nofNonTestedActions=nonTestedActions.size();
        ConditionalUtils.executeOnlyIfConditionIsTrue(nofNonTestedActions==0,
                () -> log.warning("No non tested actions"));
        return nonTestedActions.get(RandomUtils.nextInt(0,nofNonTestedActions));
    }

}
