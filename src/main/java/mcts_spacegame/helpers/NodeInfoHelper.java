package mcts_spacegame.helpers;

import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;

import java.util.List;
import java.util.Optional;

public class NodeInfoHelper {

    public static Optional<NodeInterface>  findNodeMatchingState(List<NodeInterface> nodes, State state)  {
        return nodes.stream().filter(n -> n.getState().equals(state)).findFirst();
    }

}
