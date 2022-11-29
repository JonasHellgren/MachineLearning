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

@Getter
@Log
public class NodeSelector {

    private static final int C = 1;
    public static final int UCT_MAX = 1000;
    NodeInterface nodeRoot;
    List<NodeInterface> nodesFromRootToSelected;
    List<Action> actionsFromRootToSelected;

    public NodeSelector(NodeInterface nodeRoot) {
        this.nodeRoot = nodeRoot;
        this.nodesFromRootToSelected=new ArrayList<>();
        this.actionsFromRootToSelected=new ArrayList<>();
    }

    public NodeInterface select() {
        nodesFromRootToSelected.clear();
        NodeInterface currentNode=nodeRoot;
        nodesFromRootToSelected.add(currentNode);
        while (currentNodeHasNonTerminalChilds(currentNode)) {
            if (selectChild(currentNode).isEmpty()) {
                break;
            } else {
                log.warning("No valid node selected");
                currentNode=selectChild(currentNode).get();  //todo not repear selectChild
            }

            actionsFromRootToSelected.add(currentNode.getAction());
            nodesFromRootToSelected.add(currentNode);
        }
        return currentNode;
    }

    private boolean currentNodeHasNonTerminalChilds(NodeInterface currentNode) {
        List<NodeInterface> nonTerminalNodes = getNonTerminalChildrenNodes(currentNode);
        return nonTerminalNodes.size() > 0;
    }

    public Optional<NodeInterface> selectChild(NodeInterface node) {
        List<Pair<NodeInterface,Double>> nodeUCTPairs= getListOfPairsExcludeTerminalNodes(node);
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

    private List<Pair<NodeInterface,Double>> getListOfPairsExcludeTerminalNodes(NodeInterface node) {
        List<Pair<NodeInterface,Double>> nodeUCTPairs=new ArrayList<>();
        List<NodeInterface> nonTerminalNodes = getNonTerminalChildrenNodes(node);

        for (NodeInterface childNode: nonTerminalNodes ) {
            Action actionToReachChildNode=childNode.getAction();
            double uct=calcUct(node,actionToReachChildNode);
            nodeUCTPairs.add(new Pair<>(childNode,uct));
        }
        return nodeUCTPairs;
    }

    @NotNull
    private List<NodeInterface> getNonTerminalChildrenNodes(NodeInterface node) {
        List<NodeInterface> nonTerminalNodes= node.getChildNodes().stream()
                .filter(NodeInterface::isNotTerminal)
                .collect(Collectors.toList());
        return nonTerminalNodes;
    }

    private double calcUct(NodeInterface node, Action action) {
        double v=node.getActionValue(action);
        int nParent=node.getNofVisits();
        int n=node.getNofActionSelections(action);
        return calcUct(v, nParent, n);
    }

    public double calcUct(double v, int nParent, int n) {  //good for testing
        return (MathUtils.isZero(n))
                ? UCT_MAX
                : v + C *Math.sqrt(Math.log(nParent)/ n);
    }


}
