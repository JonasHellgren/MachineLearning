package monte_carlo_tree_search.helpers;

import common.ListUtils;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import java.util.*;
import java.util.stream.Collectors;

public class NodeInfoHelper {

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

    public static <S, A> Map<A,Double> actionValueMap(ActionInterface<A> actionTemplate, NodeWithChildrenInterface<S,A> node ) {
        Map<A,Double> avMap=new HashMap<>();
        for(A a:actionTemplate.applicableActions()) {
            actionTemplate.setValue(a);
            avMap.put(a,node.getActionValue(actionTemplate));
        }
        return avMap;
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


}
