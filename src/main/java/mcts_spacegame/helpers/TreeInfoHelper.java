package mcts_spacegame.helpers;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

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
        parent=rootTree;
        for (Action action:actions) {
            Optional<NodeInterface> child=parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            parent=child.get();
        }

        return Optional.of(parent);
    }

    public Optional<List<NodeInterface>> getNodesVisitedForActions(List<Action> actionsToSelected) {

        NodeInterface parent;
        parent=rootTree;
        List<NodeInterface> nodes=new ArrayList<>();
        for (Action action:actionsToSelected) {
            Optional<NodeInterface> child=parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            nodes.add(parent);
            parent=child.get();
        }
        nodes.add(parent);

        return Optional.of(nodes);
    }

    public Optional<Double> getValueForActionInNode(List<Action> actionsToSelected, Action action) {
        Optional<NodeInterface> node=getNodeReachedForActions(actionsToSelected);

        return (node.isEmpty())
                ? Optional.empty()
                : Optional.of(node.get().getActionValue(action));

    }

    public static List<Action> getAllActions(List<Action> actionsToSelected, Action actionOnSelected) {
        List<Action> actionOnSelectedList= Collections.singletonList(actionOnSelected);
        List<Action> actions=new ArrayList<>();
        actions.addAll(actionsToSelected);
        actions.addAll(actionOnSelectedList);
        return actions;
    }

}
