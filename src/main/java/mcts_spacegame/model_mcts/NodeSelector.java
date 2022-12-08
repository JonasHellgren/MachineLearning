package mcts_spacegame.model_mcts;

import common.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_mcts_nodes.NodeNotTerminal;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/***
 * This class is for selecting nodes in selection phase, i.e. nodes on on selection path
 *
 * leaf node = node that can/shall be expanded, i.e. not tried "all" actions
 */

@Getter
@Log
public class NodeSelector {

    private static final double C_DEFAULT = 1;
    private static final boolean EXCLUDE_NEVER_VISITED_DEFAULT = false;
    public static final int UCT_MAX = 1000;
    List<NodeInterface> nodesFromRootToSelected;
    List<Action> actionsFromRootToSelected;

    final NodeInterface nodeRoot;
    private final double coefficientExploitationExploration;  //often called C in literature
    final boolean isExcludeChildrenThatNeverHaveBeenVisited;  //true when best path is desired

    public NodeSelector(NodeInterface nodeRoot) {
        this(nodeRoot, C_DEFAULT, EXCLUDE_NEVER_VISITED_DEFAULT);
    }

    public NodeSelector(NodeInterface nodeRoot, boolean isExcludeChildrenThatNeverHaveBeenVisited) {
        this(nodeRoot, C_DEFAULT, isExcludeChildrenThatNeverHaveBeenVisited);
    }

    public NodeSelector(NodeInterface nodeRoot, double coefficientExploitationExploration) {
        this(nodeRoot, coefficientExploitationExploration, EXCLUDE_NEVER_VISITED_DEFAULT);
    }

    public NodeSelector(NodeInterface nodeRoot,
                        double coefficientExploitationExploration,
                        boolean isExcludeChildrenThatNeverHaveBeenVisited) {
        this.nodeRoot = nodeRoot;  //NodeInterface.copy(nodeRoot) will not work, does not copy entire tree
        this.nodesFromRootToSelected = new ArrayList<>();
        this.actionsFromRootToSelected = new ArrayList<>();
        this.coefficientExploitationExploration = coefficientExploitationExploration;
        this.isExcludeChildrenThatNeverHaveBeenVisited = isExcludeChildrenThatNeverHaveBeenVisited;
    }

    public NodeInterface select() throws InterruptedException {
        nodesFromRootToSelected.clear();
        NodeInterface currentNode = nodeRoot;
        nodesFromRootToSelected.add(currentNode);
        while (currentNodeNotIsLeaf(currentNode)) {
            Optional<NodeInterface> selectedChild = selectChild(currentNode);
            if (childSelectionFailedAndIsNotEvaluatingBestPath(selectedChild.isEmpty())) {
                someFailPrinting();
                throw new InterruptedException("Selection failed, all children are terminal-fail, this node = "+
                        currentNode.getName() + " " +
                        "shall have been removed during defensive backup. Probably starting in hopeless state.");
            } else if (childSelectionFailedAndIsEvaluatingBestPath(selectedChild.isEmpty())) {
                log.warning("Selection failed, ok when evaluating best path. " + actionsFromRootToSelected);
                break;
            } else {
                currentNode = selectedChild.orElseThrow();
            }
            actionsFromRootToSelected.add(currentNode.getAction());
            nodesFromRootToSelected.add(currentNode);
        }
        return currentNode;
    }

    private void someFailPrinting() {
        log.warning("actionsFromRootToSelected = " + actionsFromRootToSelected);
        nodesFromRootToSelected.forEach(System.out::println);
    }

    private boolean childSelectionFailedAndIsEvaluatingBestPath(boolean failed) {
        return failed && isExcludeChildrenThatNeverHaveBeenVisited;
    }

    private boolean childSelectionFailedAndIsNotEvaluatingBestPath(boolean failed) {
        return failed && !isExcludeChildrenThatNeverHaveBeenVisited;
    }

    private boolean currentNodeNotIsLeaf(NodeInterface currentNode) {
        List<NodeInterface> childNodes = currentNode.getChildNodes();
        int nofTestedActions = childNodes.size();
        int maxNofTestedActionsToBeLeaf = MathUtils.clip(Action.applicableActions().size(), 1, Integer.MAX_VALUE);  //todo debatable
        boolean isLeaf = nofTestedActions < maxNofTestedActionsToBeLeaf;
        return !isLeaf;
    }

    public Optional<NodeInterface> selectChild(NodeInterface node) {
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
            Action actionToReachChildNode = childNode.getAction();
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

    private double calcUct(NodeInterface node, Action action) {
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
