package monte_carlo_tree_search.classes;

import common.Conditionals;
import lombok.extern.java.Log;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is for, when relevant, converting a selected no to a terminal node. This is done when all children
 * to a node is terminal-fail.
 */

@Log
public class SelectedToTerminalFailConverter<S,A> {

    NodeInterface<S,A> nodeRoot;
    List<ActionInterface<A>> actionsToSelected;

    public SelectedToTerminalFailConverter(NodeInterface<S,A> nodeRoot, List<ActionInterface<A>> actionsToSelected) {
        this.nodeRoot = nodeRoot;
        this.actionsToSelected = actionsToSelected;
    }

    //TODO remove
    public void convertSelectedNodeToFailIfAllItsChildrenAreFail(NodeInterface<S,A> nodeSelected)
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

    public boolean areAllChildrenToSelectedNodeTerminalFail(NodeInterface<S,A> nodeSelected) {
        Set<ActionInterface<A>> children = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());
        ActionInterface<A> action=nodeSelected.getAction();
        return children.size() == action.applicableActions().size();
    }

    public void makeSelectedTerminal(NodeInterface<S,A> nodeSelected) {
        log.fine("Making node = " + nodeSelected.getName() + " terminal, all its children are fail states");
        Pair<Optional<NodeInterface<S,A>>, ActionInterface<A>> parentActionPair = getParentAndActionToSelected(nodeSelected);
        Conditionals.executeOneOfTwo(parentActionPair.getFirst().isEmpty(),
                this::someErrorLogging,
                () -> transformSelectedToTerminalFail(parentActionPair.getFirst().get(), parentActionPair.getSecond(),nodeSelected));
    }

    private Pair<Optional<NodeInterface<S,A>>, ActionInterface<A>>
    getParentAndActionToSelected(NodeInterface<S,A> nodeSelected) {
        Optional<NodeInterface<S,A>> parentToSelected = Optional.empty();
        NodeInterface<S,A> nodeCurrent = nodeRoot;

       // A actionValueNA=actionsToSelected.get(0).nonApplicableAction();
        ActionInterface<A> actionToSelected=null; //=new ActionShip(actionValueNA);  //todo NOT NULL
        // ActionInterface<ShipActionSet> actionToSelected = new ActionShip(action.nonApplicableAction()); //todo generic ActionInterface<ShipActionSet>.notApplicable;

        for (ActionInterface<A> action : actionsToSelected) {
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

    private void transformSelectedToTerminalFail(NodeInterface<S,A> parentToSelected,
                                                 ActionInterface<A> actionToSelected,
                                                 NodeInterface<S,A> nodeSelected) {
        log.fine("Parent to selected is = " + parentToSelected);
        NodeInterface<S,A> selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState().copy(), actionToSelected);
        List<NodeInterface<S,A>> childrenToParent = parentToSelected.getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.addChildNode(selectedAsTerminalFail);
    }
}
