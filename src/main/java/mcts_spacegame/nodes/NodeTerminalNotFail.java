package mcts_spacegame.nodes;

import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;

public final class NodeTerminalNotFail<SSV,AV> extends NodeTerminal<SSV,AV> {
    private static final String CLASS_NAME = "T-NF";

    public NodeTerminalNotFail(StateInterface<SSV> state, ActionInterface<AV> action) {
        super(state, action);
    }

    public NodeTerminalNotFail(NodeTerminalNotFail<SSV,AV> node) {
        super(node);
    }

    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString()+CLASS_NAME);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodeTerminalNotFail)) return false;
        return super.equals(obj);
    }



}
