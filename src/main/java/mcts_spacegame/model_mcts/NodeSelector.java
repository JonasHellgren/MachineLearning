package mcts_spacegame.model_mcts;

import common.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ShipActionSet;
import org.apache.commons.math3.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/***
 * This class is for selecting nodes in selection phase, i.e. nodes on on selection path
 * The selected node must be leaf node and all its children can't be terminal
 * leaf node = node that can/shall be expanded, i.e. not tried "all" actions
 *
 * The method selectChild() returns and optional, this is empty if no child is found. Probably due to only children of
 * type fail state.
 */

@Getter
@Log
public class NodeSelector {

    private static final double C_DEFAULT = 1;
    private static final boolean EXCLUDE_NEVER_VISITED_DEFAULT = false;
    public static final int UCT_MAX = 1000;
    private static final int MAX_DEPTH = 10_000;

    final NodeInterface nodeRoot;
    MonteCarloSettings settings;
    private final double coefficientExploitationExploration;  //often called C in literature
    final boolean isExcludeChildrenThatNeverHaveBeenVisited;  //true when best path is desired

    List<NodeInterface> nodesFromRootToSelected;
    List<ActionInterface<ShipActionSet>> actionsFromRootToSelected;

    public NodeSelector(NodeInterface nodeRoot, MonteCarloSettings settings) {
        this(nodeRoot,settings, settings.coefficientExploitationExploration, EXCLUDE_NEVER_VISITED_DEFAULT);
    }

    public NodeSelector(NodeInterface nodeRoot, MonteCarloSettings settings,
                        double coefficientExploitationExploration,
                        boolean isExcludeChildrenThatNeverHaveBeenVisited) {
        this.nodeRoot = nodeRoot;
        this.settings = settings;
        this.coefficientExploitationExploration= coefficientExploitationExploration;
        this.isExcludeChildrenThatNeverHaveBeenVisited=isExcludeChildrenThatNeverHaveBeenVisited;

        this.nodesFromRootToSelected = new ArrayList<>();
        this.actionsFromRootToSelected = new ArrayList<>();
    }

    public NodeInterface select()  {
        nodesFromRootToSelected.clear();
        actionsFromRootToSelected.clear();
        NodeInterface currentNode = nodeRoot;
        nodesFromRootToSelected.add(currentNode);

        int i=0;
        while (isNotLeaf(currentNode) && notAllChildrenAreTerminal(currentNode)) {
            Optional<NodeInterface> selectedChild = selectChild(currentNode);
            if (selectedChild.isPresent() && notAllChildrenAreTerminal(currentNode)) {
                currentNode = selectedChild.get();
                actionsFromRootToSelected.add(currentNode.getAction());
                nodesFromRootToSelected.add(currentNode);
            }

            i++;
            if (i> MAX_DEPTH) {
                log.warning("Escaped from eternal loop for selecting node - can be corner case when" +
                        " isExcludeChildrenThatNeverHaveBeenVisited is true");
                break;
            }
        }

        return currentNode;
    }

    private boolean isNotLeaf(NodeInterface currentNode) {
        List<NodeInterface> childNodes = currentNode.getChildNodes();
        int nofTestedActions = childNodes.size();
        int maxNofTestedActions = settings.maxNofTestedActionsForBeingLeafFunction.apply(currentNode.getState().getVariables());
        return nofTestedActions == maxNofTestedActions;  //not leaf <=> tried all actions
    }

    private boolean notAllChildrenAreTerminal(NodeInterface node) {
        List<NodeInterface> childrenTerminal= node.getChildNodes().stream()
                .filter(n -> !n.isNotTerminal())
                .collect(Collectors.toList());
        return childrenTerminal.size()!= node.getChildNodes().size();
    }

    public  Optional<NodeInterface> selectChild(NodeInterface node) {
        List<Pair<NodeInterface, Double>> nodeUCTPairs = getListOfPairsExcludeFailNodes(node);
        Optional<Pair<NodeInterface, Double>> pair = getPairWithHighestUct(nodeUCTPairs);
        return pair.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(pair.get().getFirst());
    }

    private Optional<Pair<NodeInterface, Double>> getPairWithHighestUct(List<Pair<NodeInterface, Double>> nodeUCTPairs) {
        return nodeUCTPairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }

    private List<Pair<NodeInterface, Double>> getListOfPairsExcludeFailNodes(NodeInterface node) {
        List<Pair<NodeInterface, Double>> nodeUCTPairs = new ArrayList<>();
        List<NodeInterface> nonFailNodes = getNonFailChildrenNodes(node);
        List<NodeInterface> nodes = (isExcludeChildrenThatNeverHaveBeenVisited)
                ? getVisitedNodes(nonFailNodes)
                : nonFailNodes;

        for (NodeInterface childNode : nodes) {
            ActionInterface<ShipActionSet> actionToReachChildNode = childNode.getAction();
            double uct = calcUct(node, actionToReachChildNode);
            nodeUCTPairs.add(new Pair<>(childNode, uct));
        }
        return nodeUCTPairs;
    }

    private List<NodeInterface> getNonFailChildrenNodes(NodeInterface node) {
        return node.getChildNodes().stream()
                .filter(n -> !n.isTerminalFail())
                .collect(Collectors.toList());
    }

    private List<NodeInterface> getVisitedNodes(List<NodeInterface> nodes) {
        return nodes.stream()
                .filter(n -> n.getNofVisits() > 0)
                .collect(Collectors.toList());
    }

    private double calcUct(NodeInterface node, ActionInterface<ShipActionSet> action) {
        double v = node.getActionValue(action);
        int nParent = node.getNofVisits();
        int n = node.getNofActionSelections(action);
        return calcUct(v, nParent, n);
    }

    public double calcUct(double v, int nParent, int n) {  //good for testing => public
        return (MathUtils.isZero(n))
                ? UCT_MAX
                : v + coefficientExploitationExploration * Math.sqrt(Math.log(nParent) / n);
    }


}
