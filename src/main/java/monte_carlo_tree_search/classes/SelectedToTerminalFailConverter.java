package monte_carlo_tree_search.classes;

import common.Conditionals;
import lombok.extern.java.Log;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.exceptions.StartStateIsTrapException;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is for, when relevant, converting a selected node to a terminal node. This is done when all children
 * to a node is terminal-fail.
 */

@Log
public class SelectedToTerminalFailConverter<S,A> {

    NodeWithChildrenInterface<S,A> nodeRoot;
    List<ActionInterface<A>> actionsToSelected;
    MonteCarloSettings<S, A> settings;

    public SelectedToTerminalFailConverter(NodeWithChildrenInterface<S,A> nodeRoot,
                                           List<ActionInterface<A>> actionsToSelected,
                                           MonteCarloSettings<S, A> settings) {
        this.nodeRoot = nodeRoot;
        this.actionsToSelected = actionsToSelected;
        this.settings=settings;
    }

    //TODO remove
    public void convertSelectedNodeToFailIfAllItsChildrenAreFail(NodeWithChildrenInterface<S,A> nodeSelected)
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

    public boolean areAllChildrenToSelectedNodeTerminalFail(NodeWithChildrenInterface<S,A> nodeSelected) {
        Set<ActionInterface<A>> failActions = nodeSelected.getChildNodes().stream()
                .filter(NodeInterface::isTerminalFail).map(NodeInterface::getAction)
                .collect(Collectors.toSet());
        ActionInterface<A> action=nodeSelected.getAction();  //todo remove
       // int nofApplicActions=settings.maxNofTestedActionsForBeingLeafFunction.apply(nodeSelected.getState().getVariables());
        return failActions.size() == action.applicableActions().size();
       // return failActions.size() == nofApplicActions; // action.applicableActions().size();
    }

    public void makeSelectedTerminal(NodeInterface<S,A> nodeSelected) {
        log.fine("Making node = " + nodeSelected.getName() + " terminal, all its children are fail states");
        Pair<Optional<NodeWithChildrenInterface<S,A>>, ActionInterface<A>> parentActionPair = getParentAndActionToSelected(nodeSelected);
        Conditionals.executeOneOfTwo(parentActionPair.getFirst().isEmpty(),
                this::someErrorLogging,
                () -> transformSelectedToTerminalFail(parentActionPair.getFirst().get(), parentActionPair.getSecond(),nodeSelected));
    }

    private Pair<Optional<NodeWithChildrenInterface<S,A>>, ActionInterface<A>>
    getParentAndActionToSelected(NodeInterface<S,A> nodeSelected) {
        Optional<NodeWithChildrenInterface<S,A>> parentToSelected = Optional.empty();
        NodeWithChildrenInterface<S,A> nodeCurrent = nodeRoot;

       // A actionValueNA=actionsToSelected.get(0).nonApplicableAction();
        ActionInterface<A> actionToSelected=null; //=new ActionShip(actionValueNA);  //todo NOT NULL
        // ActionInterface<ShipActionSet> actionToSelected = new ActionShip(action.nonApplicableAction()); //todo generic ActionInterface<ShipActionSet>.notApplicable;

        for (ActionInterface<A> action : actionsToSelected) {
            boolean isSelectedChildToCurrent = nodeCurrent.getChildNodes().contains(nodeSelected);
            if (isSelectedChildToCurrent) {
                parentToSelected = Optional.of(nodeCurrent);
                actionToSelected = action;
            }
            nodeCurrent = (NodeWithChildrenInterface<S, A>) nodeCurrent.getChild(action).orElseThrow();
        }
        return new Pair<>(parentToSelected, actionToSelected);
    }

    private void someErrorLogging() {
        nodeRoot.printTree();
        log.warning("Parent to selected not found, probably children of root node are all terminal-fail");
    }

    private void transformSelectedToTerminalFail(NodeWithChildrenInterface<S,A> parentToSelected,
                                                 ActionInterface<A> actionToSelected,
                                                 NodeInterface<S,A> nodeSelected) {
        log.fine("Parent to selected is = " + parentToSelected);
        NodeInterface<S,A> selectedAsTerminalFail = NodeInterface.newTerminalFail(nodeSelected.getState().copy(), actionToSelected);
        List<NodeInterface<S,A>> childrenToParent = parentToSelected.getChildNodes();
        childrenToParent.remove(nodeSelected);
        parentToSelected.addChildNode(selectedAsTerminalFail);
    }
}
