package mcts_spacegame.model_mcts;

import common.Conditionals;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log
public class SelectedToTerminalFailConverter {

    NodeInterface nodeRoot;
    List<Action> actionsToSelected;

    public SelectedToTerminalFailConverter(NodeInterface nodeRoot, List<Action> actionsToSelected) {
        this.nodeRoot = nodeRoot;
        this.actionsToSelected = actionsToSelected;
    }

    public void convertSelectedNodeToFailIfAllItsChildrenAreFail(NodeInterface nodeSelected) throws InterruptedException {

        boolean allChildrenAreFail=areAllChildrenToSelectedNodeTerminalFail(nodeSelected);
        if (nodeSelected.equals(nodeRoot) && allChildrenAreFail) {
            nodeRoot.printTree();
            throw new InterruptedException("All children to to root node are terminal - no solution exists");
        }
        if (allChildrenAreFail) {
            makeSelectedTerminal(nodeSelected);
        }
    }

    private boolean areAllChildrenToSelectedNodeTerminalFail(NodeInterface nodeSelected) {
        Set<Action> children = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());
        return children.size() == Action.applicableActions().size();
    }

    public void makeSelectedTerminal(NodeInterface nodeSelected) {
        log.info("Making node = " + nodeSelected.getName() + " terminal, all its children are fail states");
        Pair<Optional<NodeInterface>, Action> parentActionPair = getParentAndActionToSelected(nodeSelected);
        Conditionals.executeOneOfTwo(parentActionPair.getFirst().isEmpty(),
                this::someErrorLogging,
                () -> transformSelectedToTerminalFail(parentActionPair.getFirst().get(), parentActionPair.getSecond(),nodeSelected));
    }

    private Pair<Optional<NodeInterface>, Action> getParentAndActionToSelected(NodeInterface nodeSelected) {
        Optional<NodeInterface> parentToSelected = Optional.empty();
        NodeInterface nodeCurrent = nodeRoot;
        Action actionToSelected = Action.notApplicable;
        for (Action action : actionsToSelected) {
            boolean isSelectedChildToCurrent = nodeCurrent.getChildNodes().contains(nodeSelected);
            if (isSelectedChildToCurrent) {
                parentToSelected = Optional.of(nodeCurrent);
                actionToSelected = action;
            }
            nodeCurrent = nodeCurrent.getChild(action).orElseThrow();
        }
        return new Pair<>(parentToSelected, actionToSelected);
    }

    private void someErrorLogging() {
        nodeRoot.printTree();
        log.warning("Parent to selected not found, probably children of root node are all terminal-fail");
    }

    private void transformSelectedToTerminalFail(NodeInterface parentToSelected, Action actionToSelected,NodeInterface nodeSelected) {
        log.fine("Parent to selected is = " + parentToSelected);
        NodeInterface selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState().copy(), actionToSelected);
        List<NodeInterface> childrenToParent = parentToSelected.getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.addChildNode(selectedAsTerminalFail);
    }
}
