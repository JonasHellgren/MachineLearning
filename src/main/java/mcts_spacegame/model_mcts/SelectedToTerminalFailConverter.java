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
public class SelectedToTerminalFailConverter<SSV,AV> {

    NodeInterface<SSV,AV> nodeRoot;
    List<ActionInterface<AV>> actionsToSelected;

    public SelectedToTerminalFailConverter(NodeInterface<SSV,AV> nodeRoot, List<ActionInterface<AV>> actionsToSelected) {
        this.nodeRoot = nodeRoot;
        this.actionsToSelected = actionsToSelected;
    }

    public void convertSelectedNodeToFailIfAllItsChildrenAreFail(NodeInterface<SSV,AV> nodeSelected)
            throws StartStateIsTrapException {

        boolean allChildrenAreFail=areAllChildrenToSelectedNodeTerminalFail(nodeSelected);
        if (nodeSelected.equals(nodeRoot) && allChildrenAreFail) {
            nodeRoot.printTree();
            throw new StartStateIsTrapException("All children to to root node are terminal - no solution exists");
        }
        if (allChildrenAreFail) {
            makeSelectedTerminal(nodeSelected);
        }
    }

    public boolean areAllChildrenToSelectedNodeTerminalFail(NodeInterface<SSV,AV> nodeSelected) {
        Set<ActionInterface<AV>> children = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());
        ActionInterface<AV> action=nodeSelected.getAction();
        return children.size() == action.applicableActions().size();
    }

    public void makeSelectedTerminal(NodeInterface<SSV,AV> nodeSelected) {
        log.info("Making node = " + nodeSelected.getName() + " terminal, all its children are fail states");
        Pair<Optional<NodeInterface<SSV,AV>>, ActionInterface<AV>> parentActionPair = getParentAndActionToSelected(nodeSelected);
        Conditionals.executeOneOfTwo(parentActionPair.getFirst().isEmpty(),
                this::someErrorLogging,
                () -> transformSelectedToTerminalFail(parentActionPair.getFirst().get(), parentActionPair.getSecond(),nodeSelected));
    }

    private Pair<Optional<NodeInterface<SSV,AV>>, ActionInterface<AV>>
    getParentAndActionToSelected(NodeInterface<SSV,AV> nodeSelected) {
        Optional<NodeInterface<SSV,AV>> parentToSelected = Optional.empty();
        NodeInterface<SSV,AV> nodeCurrent = nodeRoot;

       // AV actionValueNA=actionsToSelected.get(0).nonApplicableAction();
        ActionInterface<AV> actionToSelected=null; //=new ActionShip(actionValueNA);  //todo NOT NULL
        // ActionInterface<ShipActionSet> actionToSelected = new ActionShip(action.nonApplicableAction()); //todo generic ActionInterface<ShipActionSet>.notApplicable;

        for (ActionInterface<AV> action : actionsToSelected) {
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

    private void transformSelectedToTerminalFail(NodeInterface<SSV,AV> parentToSelected,
                                                 ActionInterface<AV> actionToSelected,
                                                 NodeInterface<SSV,AV> nodeSelected) {
        log.fine("Parent to selected is = " + parentToSelected);
        NodeInterface<SSV,AV> selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState().copy(), actionToSelected);
        List<NodeInterface<SSV,AV>> childrenToParent = parentToSelected.getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.addChildNode(selectedAsTerminalFail);
    }
}
