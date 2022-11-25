package mcts_spacegame.models_mcts_nodes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.Counter;

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

    public boolean isNodeWithChildren() {
        return (this instanceof NodeWithChildren);
    }

    public boolean isNodeTerminalFail() {
        return (this instanceof NodeTerminalFail);
    }

    public boolean isNodeTerminalNoFail() {
        return (this instanceof NodeTerminalNoFail);
    }


}
