package mcts_spacegame.models_mcts_nodes;

import common.ConditionalUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.Counter;

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
    int depth;
    Counter counter;
    Map<Action, Double> actionRewardMap;

    public NodeAbstract(String name,Action action) {
        this.name = name;
        this.action=action;
        depth=0;
        this.actionRewardMap=new HashMap<>();
/*        for (Action a : Action.applicableActions()) {
            actionRewardMap.put(a, INIT_REWARD_VALUE);
        }  */

    }

    protected abstract void nofOffSpringsRec(NodeInterface node, Counter counter);

    String nameAndDepthAsString() {
        return BLANK_SPACE.repeat(Math.max(0, depth)) +name;
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
        ConditionalUtils.executeOnlyIfConditionIsTrue(actionRewardMap.containsKey(action),
                () -> log.warning("Reward for action already defined"));

        actionRewardMap.put(action,reward);
    }

    public double loadRewardForAction(Action action) {
       return actionRewardMap.getOrDefault(action,INIT_REWARD_VALUE);
    }

}
