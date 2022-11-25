package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

import java.util.List;
import java.util.Set;

/***
 * A terminal node has no childrens and represents a terminal state, for ex reached goal.
 * A node is expandable if it represents a non-terminal state and if it has unvisited children.
 *
 *              NodeAbstract
 *               /    \
 *              /      \
 *   NodeWithChildren   NodeWithNoChildren (abstract)
 *                         /    \
 *                        /      \
 *           NodeTerminalFail   NodeTerminalNoFail
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

    boolean isNodeWithChildren();
    boolean isNodeTerminalFail();
    boolean isNodeTerminalNoFail();

    static NodeWithChildren newNode(State s, Action action) {
        return new NodeWithChildren(s.toString(),action);
    }

    static NodeTerminalFail newTerminalFail(State s, Action action) {
        return new NodeTerminalFail(s.toString(),action);
    }

    static NodeTerminalNoFail newTerminalNoFail(State s, Action action) {
        return new NodeTerminalNoFail(s.toString(),action);
    }



}
