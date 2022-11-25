package mcts_spacegame.model_mcts;

import lombok.Getter;

@Getter
public abstract class NodeAbstract implements NodeInterface {

    String name;
    Counter counter;

    public NodeAbstract(String name) {
        this.name = name;
    }

    protected abstract void nofOffSpringsRec(NodeInterface node, Counter counter);


}
