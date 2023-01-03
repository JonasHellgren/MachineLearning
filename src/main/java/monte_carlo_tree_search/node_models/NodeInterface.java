package monte_carlo_tree_search.node_models;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.*;

/***
 * A terminal node has no children and represents a terminal state, for ex reached goal.
 * A node is expandable if it represents a non-terminal state and if it has unvisited children.
 *
 *              NodeInterface
 *                  |
 *              NodeAbstract
 *               /    \
 *              /      \
 *   NodeNotTerminal    NodeTerminal (abstract)
 *                         /    \
 *                        /      \
 *           NodeTerminalFail   NodeTerminalNotFail
 *
 * This interface has many methods, can potentially be improved according to interface segregation principle.
 * Splitting into multiple interfaces does however require mayor refactoring and may give non clean type casts.
 * NodeWithChildrenInterface, implementing NodeInterface, is possible additional interface.
 * Would be like structure below.
 *
 *                                   NodeInterface
 *                                /        |     \
 *      NodeWithChildrenInterface  NodeAbstract    \
 *            /                                    \
 *           /                                  /    \
 *   NodeNotTerminal            NodeTerminalFail   NodeTerminalNotFail  (all extends NodeAbstract)
 *
 */

public interface NodeInterface<SSV,AV> {
    void addChildNode(NodeInterface<SSV,AV> node);
    List<NodeInterface<SSV,AV>> getChildNodes();
    int nofChildNodes();
    void printTree();
    void increaseNofVisits();
    void increaseNofActionSelections(ActionInterface<AV> a);
    void updateActionValue(double G, ActionInterface<AV> a, double alpha);
    boolean isNotTerminal();
    boolean isTerminalFail();
    boolean isTerminalNoFail();
    void saveRewardForAction(ActionInterface<AV> action, double reward);
    double restoreRewardForAction(ActionInterface<AV> action);

    int getDepth();
    String getName();
    ActionInterface<AV> getAction();
    Optional<NodeInterface<SSV,AV>> getChild(ActionInterface<AV> action);
    StateInterface<SSV> getState();
    int getNofVisits();
    int getNofActionSelections(ActionInterface<AV> a);
    double getActionValue(ActionInterface<AV> a);
    void setDepth(int depth);

    static <SSV,AV> NodeNotTerminal<SSV,AV> newNotTerminal(StateInterface<SSV> s, ActionInterface<AV> action) {
        return new NodeNotTerminal<>(s, action);
    }

    static <SSV,AV> NodeTerminalFail<SSV,AV>  newTerminalFail(StateInterface<SSV> s, ActionInterface<AV> action) {
        return new NodeTerminalFail<SSV,AV>(s,action);
    }

    static <SSV,AV> NodeTerminalNotFail<SSV,AV>  newTerminalNotFail(StateInterface<SSV> s, ActionInterface<AV> action) {
        return new NodeTerminalNotFail<SSV,AV>(s,action);
    }

    static <SSV,AV> NodeInterface<SSV,AV> newNode(StepReturnGeneric<SSV> stepReturn, ActionInterface<AV> action) {
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
    static <SSV,AV> NodeInterface<SSV,AV> copy(NodeInterface<SSV,AV>  otherNode) {
        if (otherNode instanceof NodeNotTerminal) {
            return new NodeNotTerminal<>((NodeNotTerminal<SSV, AV>) otherNode);
        } else if (otherNode instanceof NodeTerminalFail) {
            return new NodeTerminalFail<>((NodeTerminalFail<SSV,AV> ) otherNode);
        } else {
            return new NodeTerminalNotFail<>((NodeTerminalNotFail<SSV, AV>) otherNode);
        }

    }

}