package mcts_spacegame.helpers;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

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

    public Optional<List<NodeInterface>> getNodesVisitedForActions(List<Action> actions) {

        NodeInterface parent;
        parent=rootTree;
        List<NodeInterface> nodes=new ArrayList<>();
        for (Action action:actions) {
            Optional<NodeInterface> child=parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            nodes.add(parent);
            parent=child.get();
        }

        return Optional.of(nodes);
    }

}
