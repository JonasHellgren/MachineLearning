package mcts_spacegame.helpers;

import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.StateShip;

import java.util.List;
import java.util.Optional;

public class NodeInfoHelper {

    public static Optional<NodeInterface> findNodeMatchingStateVariables(List<NodeInterface> nodes, StateShip state)  {
        return nodes.stream().filter(n -> n.getState().getVariables().equals(state.getVariables())).findFirst();
    }

    public static Optional<NodeInterface>  findNodeMatchingNode(List<NodeInterface> nodes, NodeInterface node)  {
        return nodes.stream().filter(n -> n.equals(node)).findFirst();
    }

}
