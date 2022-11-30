package mcts_spacegame.helpers;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.model_mcts.NodeSelector;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;

import java.util.ArrayList;
import java.util.Collections;
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

    public List<NodeInterface> getBestPath() {
        List<NodeInterface> bestPath = new ArrayList<>();
        List<Action> actionsToSelected = getActionsOnBestPath();

        NodeInterface node = rootTree;
        for (Action action : actionsToSelected) {
            bestPath.add(node);
            node = node.getChild(action).orElseThrow();
        }
        return bestPath;
    }

    public List<Action> getActionsOnBestPath() {
        NodeSelector ns = new NodeSelector(rootTree);
        ns.select();
        return ns.getActionsFromRootToSelected();
    }

}
