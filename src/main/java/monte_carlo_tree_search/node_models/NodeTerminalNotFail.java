package monte_carlo_tree_search.node_models;

import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

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

    @Override  //todo remove
    public void saveRewardForAction(ActionInterface<AV> action, double reward) {

    }

    @Override  //todo remove
    public double restoreRewardForAction(ActionInterface<AV> action) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof NodeTerminalNotFail)) return false;
        return super.equals(obj);
    }



}
