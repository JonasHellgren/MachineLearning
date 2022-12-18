package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.State;

public final class NodeTerminalFail extends NodeTerminal {

    private static final String CLASS_NAME = "T-Fail";

    public NodeTerminalFail(State state, ShipAction action) {
        super(state, action);
    }

    public NodeTerminalFail(NodeTerminalFail node) {
        super(node);
    }

    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString()+ CLASS_NAME);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodeTerminalFail)) return false;
        return super.equals(obj);
    }


}
