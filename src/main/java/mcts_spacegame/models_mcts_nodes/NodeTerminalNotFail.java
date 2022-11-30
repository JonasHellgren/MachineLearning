package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.Action;

public final class NodeTerminalNotFail extends NodeTerminal {
    public NodeTerminalNotFail(String name, Action action) {
        super(name, action);
    }

    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString()+"(NF)");
    }
}
