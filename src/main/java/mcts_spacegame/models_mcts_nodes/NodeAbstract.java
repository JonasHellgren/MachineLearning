package mcts_spacegame.models_mcts_nodes;

import common.Conditionals;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.Counter;
import mcts_spacegame.models_space.State;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Log
public abstract class NodeAbstract implements NodeInterface {
    private static final double INIT_REWARD_VALUE = 0d;
    private static final String BLANK_SPACE = "  ";
    String name;
    Action action;
    State state;
    int depth;
    Counter counter;
    Map<Action, Double> actionRewardMap;

    public NodeAbstract(State state,Action action) {
        this.name = state.toString();
        this.action=action;
        this.state=state;
        depth=0;
        this.actionRewardMap=new HashMap<>();
/*        for (Action a : Action.applicableActions()) {
            actionRewardMap.put(a, INIT_REWARD_VALUE);
        }  */

    }

    protected abstract void nofOffSpringsRec(NodeInterface node, Counter counter);

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

    public void saveRewardForAction(Action action, double reward) {
        Conditionals.executeIfTrue(actionRewardMap.containsKey(action),
                () -> log.fine("Reward for action already defined"));

        actionRewardMap.put(action,reward);
    }

    public double restoreRewardForAction(Action action) {
       return actionRewardMap.getOrDefault(action,INIT_REWARD_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        //For each significant field in the class, check if that field matches the corresponding field of this object
        NodeAbstract equalsSample = (NodeAbstract) obj;
        boolean isSameState = equalsSample.getState().equals(this.getState());
        boolean isSameAction = equalsSample.getAction()==this.getAction();
        boolean isSameDepth = equalsSample.getDepth()==this.getDepth();

        return isSameState && isSameAction && isSameDepth;
    }

    @Override
    public String toString() {
        return  "name = "+name+
              //  ", nof ch. = " + 0+
                ", a = " + action +
                ", d = " + depth;
    }

}
