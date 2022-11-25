package mcts_spacegame.model_mcts;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

import java.util.List;

/***
 * A terminal node has no childrens and represents a terminal state, for ex reached goal.
 * A node is expandable if it represents a non-terminal state and if it has unvisited children.
 */

public interface NodeInterface {
    void addChildNode(NodeInterface node);
    List<NodeInterface> getChildNodes();
    int getDepth();
    String getName();
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
    void expand(NodeInterface childNode, Action action, double G);

    static NodeWithNoChildrens newNoChildNode(State s) {
        return new NodeWithNoChildrens(s.toString());
    }

    static NodeWithChildrens newNode(State s) {
        return new NodeWithChildrens(s.toString());
    }


}
