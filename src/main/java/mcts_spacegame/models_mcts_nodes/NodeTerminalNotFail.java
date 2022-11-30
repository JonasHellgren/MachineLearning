package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

public final class NodeTerminalNotFail extends NodeTerminal {
    public NodeTerminalNotFail(State state, Action action) {
        super(state, action);
    }

    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString()+"(T-NF)");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodeTerminalNotFail)) return false;
        return super.equals(obj);
    }

}
