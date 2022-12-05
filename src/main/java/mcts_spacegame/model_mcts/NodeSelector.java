package mcts_spacegame.model_mcts;

import common.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/***
 * leaf node = node that can/shall be expanded, i.e. not tried "all" actions
 */

@Getter
@Log
public class NodeSelector {

    private static final double C_DEFAULT = 1;
    private static final boolean EXCLUDE_NEVER_VISITED_DEFAULT=false;
    public static final int UCT_MAX = 1000;
    List<NodeInterface> nodesFromRootToSelected;
    List<Action> actionsFromRootToSelected;

    final NodeInterface nodeRoot;
    private final double coefficientExploitationExploration;  //often called C in literature
    final boolean isExcludeChildrenNeverVisited;  //true when best path is desired

    public NodeSelector(NodeInterface nodeRoot) {
        this(nodeRoot,C_DEFAULT,EXCLUDE_NEVER_VISITED_DEFAULT);
    }

    public NodeSelector(NodeInterface nodeRoot, boolean isExcludeChildrenNeverVisited ) {
        this(nodeRoot,C_DEFAULT,isExcludeChildrenNeverVisited);
    }

    public NodeSelector(NodeInterface nodeRoot, double coefficientExploitationExploration) {
        this(nodeRoot, coefficientExploitationExploration,EXCLUDE_NEVER_VISITED_DEFAULT);
    }

    public NodeSelector(NodeInterface nodeRoot, double coefficientExploitationExploration, boolean isExcludeChildrenNeverVisited) {
        this.nodeRoot = nodeRoot;  //todo copy
        this.nodesFromRootToSelected = new ArrayList<>();
        this.actionsFromRootToSelected = new ArrayList<>();
        this.coefficientExploitationExploration = coefficientExploitationExploration;
        this.isExcludeChildrenNeverVisited =isExcludeChildrenNeverVisited;
    }

    //todo constructor for setting function that test if node is leaf, i.e. sets maxNofTestedActionsToBeLeaf

    public NodeInterface select() {
        nodesFromRootToSelected.clear();
        NodeInterface currentNode = nodeRoot;
        nodesFromRootToSelected.add(currentNode);
        while (currentNodeNotIsLeaf(currentNode)) {
            if (selectChild(currentNode).isEmpty()) {
                log.warning("No valid node selected,all children are terminal-fail");
                break;
            } else {
                currentNode = selectChild(currentNode).get();  //todo not repeat selectChild
            }

            actionsFromRootToSelected.add(currentNode.getAction());
            nodesFromRootToSelected.add(currentNode);
        }
        return currentNode;
    }

    private boolean currentNodeNotIsLeaf(NodeInterface currentNode) {
        //List<NodeInterface> nonTerminalNodes = getNonTerminalChildrenNodes(currentNode);
        List<NodeInterface> childNodes = currentNode.getChildNodes();
        int nofTestedActions = childNodes.size();
        int maxNofTestedActionsToBeLeaf = MathUtils.clip(Action.applicableActions().size(),1,Integer.MAX_VALUE);  //todo debatable
        //return nonTerminalNodes.size() > 0;
        boolean isLeaf=nofTestedActions<maxNofTestedActionsToBeLeaf;
        return !isLeaf;
    }

    public Optional<NodeInterface> selectChild(NodeInterface node) {
        List<Pair<NodeInterface, Double>> nodeUCTPairs = getListOfPairsExcludeTerminalNodes(node);
        //  nodeUCTPairs.forEach(System.out::println);
        Optional<Pair<NodeInterface, Double>> pair = getPairWithHighestUct(nodeUCTPairs);

        return pair.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(pair.get().getFirst());

    }

    @NotNull
    private Optional<Pair<NodeInterface, Double>> getPairWithHighestUct(List<Pair<NodeInterface, Double>> nodeUCTPairs) {
        return nodeUCTPairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }

    private List<Pair<NodeInterface, Double>> getListOfPairsExcludeTerminalNodes(NodeInterface node) {
        List<Pair<NodeInterface, Double>> nodeUCTPairs = new ArrayList<>();
        List<NodeInterface> nonTerminalNodes = getNonFailChildrenNodes(node);
        List<NodeInterface> nodes=(isExcludeChildrenNeverVisited)
                ? getVisitedNodes(nonTerminalNodes)
                : nonTerminalNodes;

        for (NodeInterface childNode : nodes) {
            Action actionToReachChildNode = childNode.getAction();
            double uct = calcUct(node, actionToReachChildNode);
            nodeUCTPairs.add(new Pair<>(childNode, uct));
        }
        return nodeUCTPairs;
    }

    @NotNull
    private List<NodeInterface> getNonFailChildrenNodes(NodeInterface node) {
        return node.getChildNodes().stream()
                .filter(n -> !n.isTerminalFail())
                .collect(Collectors.toList());
    }

    private List<NodeInterface> getVisitedNodes(List<NodeInterface> nodes) {
        return  nodes.stream()
                .filter(n -> n.getNofVisits()>0)
                .collect(Collectors.toList());
    }

    private double calcUct(NodeInterface node, Action action) {
        double v = node.getActionValue(action);
        int nParent = node.getNofVisits();
        int n = node.getNofActionSelections(action);
        return calcUct(v, nParent, n);
    }


    public double calcUct(double v, int nParent, int n) {  //good for testing
        return (MathUtils.isZero(n))
                ? UCT_MAX
                : v + coefficientExploitationExploration * Math.sqrt(Math.log(nParent) / n);
    }


}
