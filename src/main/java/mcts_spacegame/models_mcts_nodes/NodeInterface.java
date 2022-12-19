package mcts_spacegame.models_mcts_nodes;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.environment.StepReturnREMOVE;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

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
    ShipAction getAction();
    Optional<NodeInterface> getChild(ShipAction action);
    StateInterface<ShipVariables> getState();
    int nofChildNodes();
    void printTree();
    void setDepth(int depth);
    void increaseNofVisits();
    void increaseNofActionSelections(ShipAction a);
    void updateActionValue(double G, ShipAction a, double alpha);
    int getNofVisits();
    int getNofActionSelections(ShipAction a);
    double getActionValue(ShipAction a);
    boolean isNotTerminal();
    boolean isTerminalFail();
    boolean isTerminalNoFail();
    void saveRewardForAction(ShipAction action, double reward);
    double restoreRewardForAction(ShipAction action);

    static NodeNotTerminal newNotTerminal(StateInterface<ShipVariables> s, ShipAction action) {
        return new NodeNotTerminal(s,action);
    }

    static NodeTerminalFail newTerminalFail(StateInterface<ShipVariables> s, ShipAction action) {
        return new NodeTerminalFail(s,action);
    }

    static NodeTerminalNotFail newTerminalNotFail(StateInterface<ShipVariables> s, ShipAction action) {
        return new NodeTerminalNotFail(s,action);
    }

    static NodeInterface newNode(StepReturnGeneric<ShipVariables> stepReturn, ShipAction action) {
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
