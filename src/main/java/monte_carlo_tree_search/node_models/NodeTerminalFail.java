package monte_carlo_tree_search.node_models;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NodeTerminalFail<SSV,AV> extends NodeAbstract<SSV,AV> {
    private static final String CLASS_NAME = "T-Fail";

    public NodeTerminalFail(StateInterface<SSV> state, ActionInterface<AV> action) {
        super(state, action);
    }
    public NodeTerminalFail(NodeAbstract<SSV,AV> node) {
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
