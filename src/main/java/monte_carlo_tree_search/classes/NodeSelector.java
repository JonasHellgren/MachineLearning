package monte_carlo_tree_search.classes;

import common.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
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
 * The method selectNonFailChildWithHighestUCT() returns an Optional, this is empty if no child is found.
 * Probably due to only children of type fail state.
 *
 * 1. Leaf Node. At least one non tested action. Can therefore be expanded.
 * 2. Non leaf node. Non fail. All actions tested and not all fail. Can not be expanded.
 * 3. Non leaf node. Fail. All actions tested and all fail. Can not be expanded.
 *
 * 1 or 3 -> stop loop in select
 */

@Getter
@Log
public class NodeSelector<S,A> {

    private static final double C_DEFAULT = MonteCarloSettings.C_DEFAULT;
    private static final boolean EXCLUDE_NEVER_VISITED_DEFAULT = false;
    public static final int UCT_MAX = 1000;
    private static final int MAX_DEPTH = 1000;
    private static final String ETERNAL_LOOP_MESSAGE = "Escaped from eternal loop for selecting node";
    private static final String TREE_DEPTH_EXCEEDED_MESSAGE = "Tree depth exceeded";

    final NodeWithChildrenInterface<S,A> nodeRoot;
    MonteCarloSettings<S,A> settings;
    private final double coefficientExploitationExploration;  //often called C in literature, 0 when best path is desired

    List<NodeInterface<S,A>> nodesFromRootToSelected;
    List<ActionInterface<A>> actionsFromRootToSelected;

    public NodeSelector(NodeWithChildrenInterface<S,A> nodeRoot,
                        MonteCarloSettings<S,A> settings) {
        this(nodeRoot,settings,C_DEFAULT);
    }

    public NodeSelector(NodeWithChildrenInterface<S,A> nodeRoot,
                        MonteCarloSettings<S,A> settings,
                        double coefficientExploitationExploration) {
        this.nodeRoot = nodeRoot;
        this.settings = settings;
        this.coefficientExploitationExploration= coefficientExploitationExploration;
        this.nodesFromRootToSelected = new ArrayList<>();
        this.actionsFromRootToSelected = new ArrayList<>();
    }

    public NodeWithChildrenInterface<S,A> select()  {
        nodesFromRootToSelected.clear();
        actionsFromRootToSelected.clear();
        NodeWithChildrenInterface<S,A> currentNode = nodeRoot;
        nodesFromRootToSelected.add(currentNode);
        Counter counter=new Counter(MAX_DEPTH);

        SimulationPolicyInterface<S,A> actionSelectionPolicy=settings.actionSelectionPolicy;
        while (true) {

            if (isLeafOrAllChildrenAreFail(currentNode, actionSelectionPolicy)) {
                break;
            }
            throwExceptionIfMotivated(currentNode, counter);
            currentNode = selectNonFailHighestUCTChildAsCurrent(currentNode);
            counter.increase();
        }

        return currentNode;
    }

    private void throwExceptionIfMotivated(NodeWithChildrenInterface<S, A> currentNode, Counter counter) {
        if (counter.isExceeded()) {
            throw new RuntimeException(ETERNAL_LOOP_MESSAGE);
        }

        if (NodeInfoHelper.isAtMaxDepth(currentNode,settings.maxTreeDepth)) {
            throw new RuntimeException(TREE_DEPTH_EXCEEDED_MESSAGE);
        }
    }

    private boolean isLeafOrAllChildrenAreFail(NodeWithChildrenInterface<S, A> currentNode, SimulationPolicyInterface<S, A> actionSelectionPolicy) {
        return NodeInfoHelper.isLeaf(currentNode, actionSelectionPolicy) ||
                NodeInfoHelper.isAllChildrenTerminal(currentNode);
    }

    private NodeWithChildrenInterface<S, A> selectNonFailHighestUCTChildAsCurrent(NodeWithChildrenInterface<S, A> currentNode) {
        Optional<NodeInterface<S,A>> selectedChild = selectNonFailChildWithHighestUCT(currentNode);
        if (selectedChild.isPresent()) {
            currentNode = (NodeWithChildrenInterface<S, A>) selectedChild.get();
            actionsFromRootToSelected.add(currentNode.getAction());
            nodesFromRootToSelected.add(currentNode);
        }
        return currentNode;
    }

    public  Optional<NodeInterface<S,A>> selectNonFailChildWithHighestUCT(NodeWithChildrenInterface<S,A> node) {
        List<Pair<NodeInterface<S,A>, Double>> nodeUCTPairs = getListOfPairsExcludeFailNodes(node);
        Optional<Pair<NodeInterface<S,A>, Double>> pair = getPairWithHighestUct(nodeUCTPairs);
        return pair.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(pair.get().getFirst());
    }

    private Optional<Pair<NodeInterface <S,A>, Double>> getPairWithHighestUct(List<Pair<NodeInterface <S,A>, Double>> nodeUCTPairs) {
        return nodeUCTPairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);
    }

    private List<Pair<NodeInterface <S,A>, Double>> getListOfPairsExcludeFailNodes(NodeWithChildrenInterface <S,A> node) {
        List<Pair<NodeInterface <S,A>, Double>> nodeUCTPairs = new ArrayList<>();
        List<NodeInterface <S,A>> nonFailNodes = getNonFailChildrenNodes(node);

        for (NodeInterface <S,A> childNode : nonFailNodes) {
            ActionInterface<A> actionToReachChildNode = childNode.getAction();
            double uct = calcUct(node, actionToReachChildNode);
            nodeUCTPairs.add(new Pair<>(childNode, uct));
        }
        return nodeUCTPairs;
    }

    private List<NodeInterface <S,A>> getNonFailChildrenNodes(NodeWithChildrenInterface <S,A> node) {
        return node.getChildNodes().stream()
                .filter(n -> !n.isTerminalFail())
                .collect(Collectors.toList());
    }

    private double calcUct(NodeWithChildrenInterface <S,A> node, ActionInterface<A> action) {
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
