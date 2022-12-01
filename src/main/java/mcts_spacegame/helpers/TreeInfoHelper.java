package mcts_spacegame.helpers;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.model_mcts.NodeSelector;
import mcts_spacegame.models_mcts_nodes.Counter;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreeInfoHelper {

    NodeInterface rootTree;

    public TreeInfoHelper(NodeInterface rootTree) {
        this.rootTree = rootTree;
    }


    //todo use getNodesVisitedForActions
    public Optional<NodeInterface> getNodeReachedForActions(List<Action> actions) {

        NodeInterface parent;
        parent = rootTree;
        for (Action action : actions) {
            Optional<NodeInterface> child = parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            parent = child.get();
        }

        return Optional.of(parent);
    }

    public Optional<List<NodeInterface>> getNodesOnPathForActions(List<Action> actionsToSelected) {

        NodeInterface parent;
        parent = rootTree;
        List<NodeInterface> nodes = new ArrayList<>();
        for (Action action : actionsToSelected) {
            Optional<NodeInterface> child = parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            nodes.add(parent);
            parent = child.get();
        }
        nodes.add(parent);

        return Optional.of(nodes);
    }

    public Optional<Double> getValueForActionInNode(List<Action> actionsToSelected, Action action) {
        Optional<NodeInterface> node = getNodeReachedForActions(actionsToSelected);

        return (node.isEmpty())
                ? Optional.empty()
                : Optional.of(node.get().getActionValue(action));

    }

    public static State getState(State rootState, Environment environment, List<Action> actionsToSelected) {
        State state = rootState.copy();
        for (Action a : actionsToSelected) {
            StepReturn sr = stepAndUpdateState(environment, state, a);
        }
        return state;
    }

    private static StepReturn stepAndUpdateState(Environment environment, State pos, Action a) {
        StepReturn sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }

    public List<NodeInterface> getBestPath(double C) {
        NodeSelector ns = new NodeSelector(rootTree,C);
        ns.select();
        return ns.getNodesFromRootToSelected();
    }

    public int nofNodesInTree() {
        Counter counter = new Counter();
        nofOffSpringsRecursive(rootTree,counter);
        return counter.value();
    }

    void nofOffSpringsRecursive(NodeInterface node, Counter counter) {
        for (NodeInterface  child:node.getChildNodes()) {
            counter.increment();
            nofOffSpringsRecursive(child,counter);
        }
    }

}
