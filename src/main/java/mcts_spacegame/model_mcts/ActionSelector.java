package mcts_spacegame.model_mcts;

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
        if(nofNonTestedActions==0) {
            log.warning("No non-tested actions");
            List<Action> testedActions = getTestedActions();
            int nofTestedActions=testedActions.size();

            if (nofTestedActions==0) {
                throw new RuntimeException("nofTestedActions=0");
            }

            return testedActions.get(RandomUtils.nextInt(0, nofTestedActions));
        } else {
            return nonTestedActions.get(RandomUtils.nextInt(0, nofNonTestedActions));
        }
    }

    @NotNull
    private List<Action> getNonTestedActions() {
        List<Action> testedActions = getTestedActions();
        List<Action> nonTestedActions=new ArrayList<>(Action.applicableActions());  //must be mutable
        nonTestedActions.removeAll(testedActions);
        return nonTestedActions;
    }

    @NotNull
    private List<Action> getTestedActions() {
        return nodeSelected.getChildNodes().stream()
                .map(NodeInterface::getAction).collect(Collectors.toList());
    }

}
