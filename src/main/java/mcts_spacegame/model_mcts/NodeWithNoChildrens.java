package mcts_spacegame.model_mcts;

import lombok.SneakyThrows;
import mcts_spacegame.enums.Action;

import java.util.Collections;
import java.util.List;

public class NodeWithNoChildrens extends NodeAbstract {  //todo TerminalLeaf

    public NodeWithNoChildrens(String name) {
        super(name);
    }

    @Override
    protected void nofOffSpringsRec(NodeInterface node, Counter counter) {
    }

    @Override
    @SneakyThrows
    public void addChildNode(NodeInterface node) {
        throw new NoSuchMethodException("Can't add child to person without child");
    }

    @Override
    public List<NodeInterface> getChildNodes() {
        return Collections.emptyList();
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

    @Override
    public void printTree() {
        System.out.println(super.getName());
    }

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
}
