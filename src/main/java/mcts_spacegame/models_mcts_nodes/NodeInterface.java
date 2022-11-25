package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.Action;
import mcts_spacegame.models_space.State;

import java.util.List;
import java.util.Set;

/***
 * A terminal node has no children and represents a terminal state, for ex reached goal.
 * A node is expandable if it represents a non-terminal state and if it has unvisited children.
 *
 *              NodeAbstract
 *               /    \
 *              /      \
 *   NodeNotTerminal    NodeTerminal (abstract)
 *                         /    \
 *                        /      \
 *           NodeTerminalFail   NodeTerminalNotFail
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

    boolean isNotTerminal();
    boolean isTerminalFail();
    boolean isTerminalNoFail();

    static NodeNotTerminal newNotTerminal(State s, Action action) {
        return new NodeNotTerminal(s.toString(),action);
    }

    static NodeTerminalFail newTerminalFail(State s, Action action) {
        return new NodeTerminalFail(s.toString(),action);
    }

    static NodeTerminalNotFail newTerminalNotFail(State s, Action action) {
        return new NodeTerminalNotFail(s.toString(),action);
    }



}
