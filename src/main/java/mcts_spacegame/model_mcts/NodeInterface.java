package mcts_spacegame.model_mcts;

import java.util.List;

public interface NodeInterface {
    void addChildNode(NodeInterface node);
    List<NodeInterface> getChildNodes();
    //Integer getIndex();
    String getName();
    int nofChildNodes();
    int nofSubNodes();
    void printTree();

    void increaseNofVisits();
    void increaseNofActionSelections(int a);
    void updateActionValue(double G, int a);
    int getNofVisits();
    int getNofActionSelections(int a);
    double getActionValue(int a);
}
