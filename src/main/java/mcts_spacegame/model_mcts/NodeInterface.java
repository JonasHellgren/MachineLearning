package mcts_spacegame.model_mcts;

import mcts_spacegame.enums.Action;

import java.util.List;

/***
 * A terminal node has no childrens and represents a terminal state, for ex reached goal.
 * A node is expandable if it represents a non-terminal state and if it has unvisited children.
 */

public interface NodeInterface {
    void addChildNode(NodeInterface node);
    List<NodeInterface> getChildNodes();
    //Integer getIndex();
    String getName();
    int nofChildNodes();
    int nofSubNodes();
    void printTree();

    void increaseNofVisits();
    void increaseNofActionSelections(Action a);
    void updateActionValue(double G, Action a);
    int getNofVisits();
    int getNofActionSelections(Action a);
    double getActionValue(Action a);
}
