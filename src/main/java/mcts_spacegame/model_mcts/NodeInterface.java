package mcts_spacegame.model_mcts;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

import java.util.List;
import java.util.Set;

/***
 * A terminal node has no childrens and represents a terminal state, for ex reached goal.
 * A node is expandable if it represents a non-terminal state and if it has unvisited children.
 */

public interface NodeInterface {
    void addChildNode(NodeInterface node);
    List<NodeInterface> getChildNodes();
    int getDepth();
    String getName();
    Action getAction();
    int nofChildNodes();
    int nofSubNodes();
    void printTree();

    void setDepth(int depth);
    void increaseNofVisits();
    void increaseNofActionSelections(Action a);
    void updateActionValue(double G, Action a);
    int getNofVisits();
    int getNofActionSelections(Action a);
    double getActionValue(Action a);
    Set<Action> getActionSet();
    void expand(NodeInterface childNode, Action action);

    static NodeWithNoChildrens newNoChildNode(State s,Action action) {
        return new NodeWithNoChildrens(s.toString(),action);
    }

    static NodeWithChildrens newNode(State s,Action action) {
        return new NodeWithChildrens(s.toString(),action);
    }


}
