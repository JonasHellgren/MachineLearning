package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.Action;

public final class NodeTerminalFail extends NodeTerminal {
    public NodeTerminalFail(String name, Action action) {
        super(name, action);
    }

    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString()+"(TF)");
    }
}
