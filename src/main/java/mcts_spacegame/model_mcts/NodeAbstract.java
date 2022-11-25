package mcts_spacegame.model_mcts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class NodeAbstract implements NodeInterface {

    private static final String BLANK_SPACE = "  ";
    String name;
    int depth;
    Counter counter;

    public NodeAbstract(String name) {
        this.name = name;
        depth=0;
    }

    protected abstract void nofOffSpringsRec(NodeInterface node, Counter counter);

    String nameAndDepthAsString() {
        return BLANK_SPACE.repeat(Math.max(0, depth)) +name;
    }

}
