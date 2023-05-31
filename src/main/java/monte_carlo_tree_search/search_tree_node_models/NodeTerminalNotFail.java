package monte_carlo_tree_search.search_tree_node_models;

import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

public final class NodeTerminalNotFail<SSV,AV> extends NodeAbstract<SSV,AV> {
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
