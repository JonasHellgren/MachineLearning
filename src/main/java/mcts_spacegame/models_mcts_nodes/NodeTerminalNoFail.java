package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.Action;

public final class NodeTerminalNoFail extends NodeWithNoChildren{
    public NodeTerminalNoFail(String name, Action action) {
        super(name, action);
    }
}
