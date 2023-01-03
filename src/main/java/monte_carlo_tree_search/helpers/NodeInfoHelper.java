package monte_carlo_tree_search.helpers;

import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.domains.models_space.StateShip;

import java.util.List;
import java.util.Optional;

public class NodeInfoHelper<SSV, AV> {

    public static <SSV, AV> Optional<NodeInterface<SSV, AV>> findNodeMatchingStateVariables(List<NodeInterface<SSV, AV>> nodes, StateShip state)  {
        return nodes.stream().filter(n -> n.getState().getVariables().equals(state.getVariables())).findFirst();
    }

    public static <SSV, AV> Optional<NodeInterface<SSV, AV>>  findNodeMatchingNode(List<NodeInterface<SSV, AV>> nodes, NodeInterface<SSV, AV> node)  {
        return nodes.stream().filter(n -> n.equals(node)).findFirst();
    }

}