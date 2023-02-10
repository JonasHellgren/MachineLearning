package monte_carlo_tree_search.node_models;

import common.Conditionals;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.*;

@Getter
@Setter
@Log
public abstract class NodeAbstract<SSV,AV> implements NodeInterface<SSV,AV> {

    private static final String BLANK_SPACE = "  ";
    String name;
    ActionInterface<AV> action;
    StateInterface<SSV> state;
    int depth;

    public NodeAbstract(StateInterface<SSV> state, ActionInterface<AV> action) {
        this.name = state.toString();
        this.action=action;
        this.state=state.copy();
        this.depth=0;
    }

    public NodeAbstract(String name,
                        ActionInterface<AV> action,
                        StateInterface<SSV> state,
                        int depth) {
        this.name = name;
        this.action = action;
        this.state = state;
        this.depth = depth;
    }

    public NodeAbstract(NodeAbstract<SSV,AV> node) {
        this(node.name,node.action,node.state,node.depth);
    }

    String nameAndDepthAsString() {
        return BLANK_SPACE.repeat(Math.max(0, depth)) + name+","+action+",";
    }

    public boolean isNotTerminal() {
        return (this instanceof NodeNotTerminal);
    }

    public boolean isTerminalFail() {
        return (this instanceof NodeTerminalFail);
    }

    public boolean isTerminalNoFail() {
        return (this instanceof NodeTerminalNotFail);
    }


    @Override
    public boolean equals(Object obj) {
        //For each significant field in the class, check if that field matches the corresponding field of this object
        NodeAbstract<SSV,AV> equalsSample = (NodeAbstract) obj;
        boolean isSameState = equalsSample.getState().getVariables().equals(this.getState().getVariables());
        boolean isSameAction = equalsSample.getAction().getValue()==this.getAction().getValue();
        boolean isSameDepth = equalsSample.getDepth()==this.getDepth();
        return isSameState && isSameAction && isSameDepth;
    }

    @Override
    public String toString() {
        return  "name = "+name+
              //  ", nof ch. = " + this.nofChildNodes()+
                ", a = " + action +
                ", d = " + depth;
    }

}
