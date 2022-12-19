package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

public final class NodeTerminalNotFail extends NodeTerminal {
    private static final String CLASS_NAME = "T-NF";

    public NodeTerminalNotFail(StateInterface<ShipVariables> state, ActionInterface<ShipActionSet> action) {
        super(state, action);
    }

    public NodeTerminalNotFail(NodeTerminalNotFail node) {
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
