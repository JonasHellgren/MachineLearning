package monte_carlo_tree_search.helpers;

import common.ListUtils;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.domains.models_space.StateShip;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeInfoHelper<S, A> {

    public static <S, A> Optional<NodeInterface<S, A>>
    findNodeMatchingStateVariables(List<NodeInterface<S, A>> nodes,
                                   StateInterface<S> state)  {
        return nodes.stream().filter(n -> n.getState().getVariables().equals(state.getVariables())).findFirst();
    }

    public static <S, A> Optional<NodeInterface<S, A>>  findNodeMatchingNode(
            List<NodeInterface<S, A>> nodes,
            NodeInterface<S, A> node)  {
        return nodes.stream().filter(n -> n.equals(node)).findFirst();
    }

    public static <S, A> double valueNode(ActionInterface<A> actionTemplate, NodeWithChildrenInterface<S,A> node ) {
        List<Double> avs=actionValuesNode(actionTemplate,node);
        return ListUtils.findMax(avs).orElse(0);
    }

    public static <S, A> List<Double> actionValuesNode(ActionInterface<A> actionTemplate, NodeWithChildrenInterface<S,A> node ) {
        List<Double> avs=new ArrayList<>();
        for(A a:actionTemplate.applicableActions()) {
            actionTemplate.setValue(a);
            avs.add(node.getActionValue(actionTemplate));
        }
        return avs;
    }

    /**
     * leaf node = node that  can/shall be expanded, i.e. not tried "all" actions
     * selected node shall be leaf node =>  isLeaf(selectedNode) = true
     */

    public static <S, A> boolean isLeaf(NodeWithChildrenInterface<S, A> currentNode, SimulationPolicyInterface<S,A> actionSelectionPolicy) {
        List<NodeInterface<S,A>> childNodes = currentNode.getChildNodes();
        int nofTestedActions = childNodes.size();
        int maxNofTestedActions = actionSelectionPolicy.availableActionValues(currentNode.getState()).size();
        return nofTestedActions < maxNofTestedActions;  //leaf <=> not tried all actions
    }

    public static <S, A> boolean isAllChildrenTerminal(NodeWithChildrenInterface<S,A> node) {
        List<NodeInterface<S,A>> childrenTerminal= node.getChildNodes().stream()
                .filter(n -> !n.isNotTerminal())
                .collect(Collectors.toList());
        return childrenTerminal.size()== node.getChildNodes().size();
    }

    public static <S, A> boolean isAtMaxDepth(NodeWithChildrenInterface<S,A> node, int maxTreeDepth) {
        return  node.getDepth()==maxTreeDepth;
    }


    //todo remove, valueNode does the same
    public static <S, A> A bestActionValue(ActionInterface<A> actionTemplate, NodeWithChildrenInterface<S,A> node ) {
        List<Pair<A, Double>> actionValuePairs = new ArrayList<>();
        for(A a:actionTemplate.applicableActions()) {
            actionTemplate.setValue(a);
            actionValuePairs.add(new Pair<>(a, node.getActionValue(actionTemplate)));
        }

        Optional<Pair<A, Double>> bestPair=actionValuePairs.stream().
                reduce((res, item) -> res.getSecond() > item.getSecond() ? res : item);

        return bestPair.orElseThrow().getFirst();
    }

}
