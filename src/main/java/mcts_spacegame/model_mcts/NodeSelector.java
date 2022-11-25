package mcts_spacegame.model_mcts;

import common.MathUtils;
import lombok.Getter;
import mcts_spacegame.enums.Action;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class NodeSelector {

    private static final int C = 1;
    public static final int UCT_MAX = 1000;
    NodeInterface nodeRoot;
    List<NodeInterface> nodesFromRootToSelected;

    public NodeSelector(NodeInterface nodeRoot) {
        this.nodeRoot = nodeRoot;
        this.nodesFromRootToSelected=new ArrayList<>();
    }

    public NodeInterface findNode() {
        nodesFromRootToSelected.clear();
        NodeInterface currentNode=nodeRoot;
        nodesFromRootToSelected.add(currentNode);
        while (currentNode.nofChildNodes()!=0) {
            currentNode=selectChild(currentNode);
            nodesFromRootToSelected.add(currentNode);
        }
        return currentNode;
    }

    public NodeInterface selectChild(NodeInterface node) {
        List<Pair<NodeInterface,Double>> nodeUCTPairs=getListOfPairs(node);
      //  nodeUCTPairs.forEach(System.out::println);
        Optional<Pair<NodeInterface, Double>> pair = getPairWithHighestUct(nodeUCTPairs);
        return pair.orElseThrow().getFirst();
    }

    @NotNull
    private Optional<Pair<NodeInterface, Double>> getPairWithHighestUct(List<Pair<NodeInterface, Double>> nodeUCTPairs) {
        return nodeUCTPairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }

    private List<Pair<NodeInterface,Double>>  getListOfPairs(NodeInterface node) {
        List<Pair<NodeInterface,Double>> nodeUCTPairs=new ArrayList<>();
        for (NodeInterface childNode: node.getChildNodes() ) {
            Action actionToReachChildNode=childNode.getAction();
            double uct=calcUct(node,actionToReachChildNode);
            nodeUCTPairs.add(new Pair<>(childNode,uct));
        }
        return nodeUCTPairs;
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
