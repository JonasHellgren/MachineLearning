package mcts_spacegame.model_mcts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mcts_spacegame.enums.Action;

@Getter
@Setter
@EqualsAndHashCode
public abstract class NodeAbstract implements NodeInterface {

    private static final String BLANK_SPACE = "  ";
    String name;
    Action action;
    int depth;
    Counter counter;

    public NodeAbstract(String name,Action action) {
        this.name = name;
        this.action=action;
        depth=0;
    }

    protected abstract void nofOffSpringsRec(NodeInterface node, Counter counter);

    String nameAndDepthAsString() {
        return BLANK_SPACE.repeat(Math.max(0, depth)) +name;
    }



}
