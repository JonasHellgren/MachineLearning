package monte_carlo_tree_search.classes;

import common.MathUtils;
import lombok.Getter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
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
 * The method selectChild() returns an Optional, this is empty if no child is found. Probably due to only children of
 * type fail state.
 */

@Getter
@Log
public class NodeSelector<S,A> {

    private static final double C_DEFAULT = 1;
    private static final boolean EXCLUDE_NEVER_VISITED_DEFAULT = false;
    public static final int UCT_MAX = 1000;
    private static final int MAX_DEPTH = 10_000;

    final NodeWithChildrenInterface<S,A> nodeRoot;
    MonteCarloSettings<S,A> settings;
    private final double coefficientExploitationExploration;  //often called C in literature
    final boolean isExcludeChildrenThatNeverHaveBeenVisited;  //true when best path is desired

    List<NodeInterface<S,A>> nodesFromRootToSelected;
    List<ActionInterface<A>> actionsFromRootToSelected;

    public NodeSelector(NodeWithChildrenInterface<S,A> nodeRoot, MonteCarloSettings<S,A> settings) {
        this(nodeRoot,settings, settings.coefficientExploitationExploration, EXCLUDE_NEVER_VISITED_DEFAULT);
    }

    public NodeSelector(NodeWithChildrenInterface<S,A> nodeRoot, MonteCarloSettings<S,A> settings,
                        double coefficientExploitationExploration,
                        boolean isExcludeChildrenThatNeverHaveBeenVisited) {
        this.nodeRoot = nodeRoot;
        this.settings = settings;
        this.coefficientExploitationExploration= coefficientExploitationExploration;
        this.isExcludeChildrenThatNeverHaveBeenVisited=isExcludeChildrenThatNeverHaveBeenVisited;

        this.nodesFromRootToSelected = new ArrayList<>();
        this.actionsFromRootToSelected = new ArrayList<>();
    }

    public NodeWithChildrenInterface<S,A> select()  {
        nodesFromRootToSelected.clear();
        actionsFromRootToSelected.clear();
        NodeWithChildrenInterface<S,A> currentNode = nodeRoot;
        nodesFromRootToSelected.add(currentNode);

        int i=0;
        while (isNotLeaf(currentNode) && isNotAllChildrenTerminal(currentNode)) {
            Optional<NodeInterface<S,A>> selectedChild = selectChild(currentNode);
            if (selectedChild.isPresent() && isNotAllChildrenTerminal(currentNode)) {
                currentNode = (NodeWithChildrenInterface<S, A>) selectedChild.get();
                actionsFromRootToSelected.add(currentNode.getAction());
                nodesFromRootToSelected.add(currentNode);
            }

            i++;
            if (i> MAX_DEPTH) {
                log.fine("Escaped from eternal loop for selecting node - can be corner case when" +
                        " isExcludeChildrenThatNeverHaveBeenVisited is true");
                break;
            }
        }

        return currentNode;
    }

    private boolean isNotLeaf(NodeWithChildrenInterface<S,A> currentNode) {
        List<NodeInterface<S,A>> childNodes = currentNode.getChildNodes();
        int nofTestedActions = childNodes.size();
        int maxNofTestedActions =
                settings.maxNofTestedActionsForBeingLeafFunction.apply(currentNode.getState().getVariables());
        return nofTestedActions == maxNofTestedActions;  //not leaf <=> tried all actions
    }

    private boolean isNotAllChildrenTerminal(NodeWithChildrenInterface<S,A> node) {
        List<NodeInterface<S,A>> childrenTerminal= node.getChildNodes().stream()
                .filter(n -> !n.isNotTerminal())
                .collect(Collectors.toList());
        return childrenTerminal.size()!= node.getChildNodes().size();
    }

    public  Optional<NodeInterface<S,A>> selectChild(NodeWithChildrenInterface<S,A> node) {
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
        List<NodeInterface <S,A>> nodes = (isExcludeChildrenThatNeverHaveBeenVisited)
                ? getVisitedNodes(nonFailNodes)
                : nonFailNodes;

        for (NodeInterface <S,A> childNode : nodes) {
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

    private List<NodeInterface <S,A>> getVisitedNodes(List<NodeInterface <S,A>> nodes) {
        return nodes.stream()
                .filter(n -> n.getNofVisits() > 0)
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
