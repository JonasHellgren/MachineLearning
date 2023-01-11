package monte_carlo_tree_search.helpers;

import common.ListUtils;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.domains.models_space.StateShip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NodeInfoHelper<S, A> {

    public static <S, A> Optional<NodeInterface<S, A>> findNodeMatchingStateVariables(List<NodeInterface<S, A>> nodes,
                                                                                            StateShip state)  {
        return nodes.stream().filter(n -> n.getState().getVariables().equals(state.getVariables())).findFirst();
    }

    public static <S, A> Optional<NodeInterface<S, A>>  findNodeMatchingNode(List<NodeInterface<S, A>> nodes,
                                                                                   NodeInterface<S, A> node)  {
        return nodes.stream().filter(n -> n.equals(node)).findFirst();
    }

    public static <S, A> double valueNode(ActionInterface<A> actionTemplate, NodeInterface<S,A> node ) {
        List<Double> avs=new ArrayList<>();
        for(A a:actionTemplate.applicableActions()) {
            actionTemplate.setValue(a);
            avs.add(node.getActionValue(actionTemplate));
        }
        return ListUtils.findMax(avs).orElse(0);
    }

}
