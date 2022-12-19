package mcts_spacegame.model_mcts;

import common.Conditionals;
import lombok.extern.java.Log;
import mcts_spacegame.exceptions.StartStateIsTrapException;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ActionShip;
import mcts_spacegame.models_space.ShipActionSet;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log
public class SelectedToTerminalFailConverter {

    NodeInterface nodeRoot;
    List<ActionInterface<ShipActionSet>> actionsToSelected;

    public SelectedToTerminalFailConverter(NodeInterface nodeRoot, List<ActionInterface<ShipActionSet>> actionsToSelected) {
        this.nodeRoot = nodeRoot;
        this.actionsToSelected = actionsToSelected;
    }

    public void convertSelectedNodeToFailIfAllItsChildrenAreFail(NodeInterface nodeSelected) throws StartStateIsTrapException {

        boolean allChildrenAreFail=areAllChildrenToSelectedNodeTerminalFail(nodeSelected);
        if (nodeSelected.equals(nodeRoot) && allChildrenAreFail) {
            nodeRoot.printTree();
            throw new StartStateIsTrapException("All children to to root node are terminal - no solution exists");
        }
        if (allChildrenAreFail) {
            makeSelectedTerminal(nodeSelected);
        }
    }

    public boolean areAllChildrenToSelectedNodeTerminalFail(NodeInterface nodeSelected) {
        Set<ActionInterface<ShipActionSet>> children = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());
        return children.size() == ShipActionSet.applicableActions().size();
    }

    public void makeSelectedTerminal(NodeInterface nodeSelected) {
        log.info("Making node = " + nodeSelected.getName() + " terminal, all its children are fail states");
        Pair<Optional<NodeInterface>, ActionInterface<ShipActionSet>> parentActionPair = getParentAndActionToSelected(nodeSelected);
        Conditionals.executeOneOfTwo(parentActionPair.getFirst().isEmpty(),
                this::someErrorLogging,
                () -> transformSelectedToTerminalFail(parentActionPair.getFirst().get(), parentActionPair.getSecond(),nodeSelected));
    }

    private Pair<Optional<NodeInterface>, ActionInterface<ShipActionSet>> getParentAndActionToSelected(NodeInterface nodeSelected) {
        Optional<NodeInterface> parentToSelected = Optional.empty();
        NodeInterface nodeCurrent = nodeRoot;
        ActionInterface<ShipActionSet> actionToSelected = new ActionShip(ShipActionSet.notApplicable); //todo generic ActionInterface<ShipActionSet>.notApplicable;
        for (ActionInterface<ShipActionSet> action : actionsToSelected) {
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

    private void transformSelectedToTerminalFail(NodeInterface parentToSelected, ActionInterface<ShipActionSet> actionToSelected, NodeInterface nodeSelected) {
        log.fine("Parent to selected is = " + parentToSelected);
        NodeInterface selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState().copy(), actionToSelected);
        List<NodeInterface> childrenToParent = parentToSelected.getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.addChildNode(selectedAsTerminalFail);
    }
}
