package mcts_spacegame.models_mcts_nodes;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.Counter;
import mcts_spacegame.models_space.State;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log
public abstract class NodeTerminal extends NodeAbstract {  //todo TerminalLeaf

    public NodeTerminal(State state, Action action) {
        super(state,action);
    }

    @Override
    protected void nofOffSpringsRec(NodeInterface node, Counter counter) {
    }

    @Override
    @SneakyThrows
    public void addChildNode(NodeInterface node) {
        log.warning("Can't add child to node without child");
    }

    @Override
    public List<NodeInterface> getChildNodes() {
        return Collections.emptyList();
    }

    @Override
    public Optional<NodeInterface> getChild(Action action) {
        return Optional.empty();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int nofChildNodes() {
        return 0;
    }

    @Override
    public int nofSubNodes() {
        return 0;
    }

    /*
    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString()+"(T)");
    }  */

    @Override
    public void increaseNofVisits() {

    }

    @Override
    public void increaseNofActionSelections(Action a) {

    }

    @Override
    public void updateActionValue(double G, Action a) {

    }

    @Override
    public int getNofVisits() {
        return 0;
    }

    @Override
    public int getNofActionSelections(Action a) {
        return 0;
    }

    @Override
    public double getActionValue(Action a) {
        return 0;
    }

    @Override
    public Set<Action> getActionSet() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return  "name = "+name+
                "nof childs = " + 0;
    }

}
