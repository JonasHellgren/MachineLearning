package mcts_spacegame.nodes;

import common.Conditionals;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Log
public abstract class NodeAbstract<SSV,AV> implements NodeInterface<SSV,AV> {
    private static final double INIT_REWARD_VALUE = 0d;
    private static final String BLANK_SPACE = "  ";
    String name;
    ActionInterface<AV> action;
    StateInterface<SSV> state;
    int depth;
    Map<AV, Double> actionRewardMap;

    public NodeAbstract(StateInterface<SSV> state, ActionInterface<AV> action) {
        this.name = state.toString();
        this.action=action;
        this.state=state.copy();
        this.depth=0;
        this.actionRewardMap=new HashMap<>();
    }

    public NodeAbstract(String name,
                        ActionInterface<AV> action,
                        StateInterface<SSV> state,
                        int depth,
                        Map<AV, Double> actionRewardMap) {
        this.name = name;
        this.action = action;
        this.state = state;
        this.depth = depth;
        this.actionRewardMap = new HashMap<>(actionRewardMap);
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

    public void saveRewardForAction(ActionInterface<AV> action, double reward) {
        Conditionals.executeIfTrue(actionRewardMap.containsKey(action.getValue()),
                () -> log.fine("Reward for action already defined"));

        actionRewardMap.put(action.getValue(),reward);
    }

    public double restoreRewardForAction(ActionInterface<AV> action) {
       return actionRewardMap.getOrDefault(action.getValue(),INIT_REWARD_VALUE);
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
                ", nof ch. = " + this.nofChildNodes()+
                ", a = " + action +
                ", d = " + depth;
    }

}
