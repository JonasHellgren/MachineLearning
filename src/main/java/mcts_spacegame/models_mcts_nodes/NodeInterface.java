package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

import java.util.*;

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
    ActionInterface<ShipActionSet> getAction();
    Optional<NodeInterface> getChild(ActionInterface<ShipActionSet> action);
    StateInterface<ShipVariables> getState();
    int nofChildNodes();
    void printTree();
    void setDepth(int depth);
    void increaseNofVisits();
    void increaseNofActionSelections(ActionInterface<ShipActionSet> a);
    void updateActionValue(double G, ActionInterface<ShipActionSet> a, double alpha);
    int getNofVisits();
    int getNofActionSelections(ActionInterface<ShipActionSet> a);
    double getActionValue(ActionInterface<ShipActionSet> a);
    boolean isNotTerminal();
    boolean isTerminalFail();
    boolean isTerminalNoFail();
    void saveRewardForAction(ActionInterface<ShipActionSet> action, double reward);
    double restoreRewardForAction(ActionInterface<ShipActionSet> action);

    static NodeNotTerminal newNotTerminal(StateInterface<ShipVariables> s, ActionInterface<ShipActionSet> action) {
        return new NodeNotTerminal(s,action);
    }

    static NodeTerminalFail newTerminalFail(StateInterface<ShipVariables> s, ActionInterface<ShipActionSet> action) {
        return new NodeTerminalFail(s,action);
    }

    static NodeTerminalNotFail newTerminalNotFail(StateInterface<ShipVariables> s, ActionInterface<ShipActionSet> action) {
        return new NodeTerminalNotFail(s,action);
    }

    static NodeInterface newNode(StepReturnGeneric<ShipVariables> stepReturn, ActionInterface<ShipActionSet> action) {
        if (!stepReturn.isTerminal) {
            return newNotTerminal(stepReturn.newState, action);
        } else if (stepReturn.isFail) {
            return newTerminalFail(stepReturn.newState, action);
        } else {
            return newTerminalNotFail(stepReturn.newState, action);
        }

    }

    /**
     * Warning, only copies node, not sub nodes
     */
    static NodeInterface copy(NodeInterface otherNode) {
        if (otherNode instanceof NodeNotTerminal) {
            return new NodeNotTerminal((NodeNotTerminal) otherNode);
        } else if (otherNode instanceof NodeTerminalFail) {
            return new NodeTerminalFail((NodeTerminalFail) otherNode);
        } else {
            return new NodeTerminalNotFail((NodeTerminalNotFail) otherNode);
        }

    }

}
