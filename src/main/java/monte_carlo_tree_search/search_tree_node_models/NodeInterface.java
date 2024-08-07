package monte_carlo_tree_search.search_tree_node_models;

import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.*;

/***
 * A terminal node has no children and represents a terminal stateNew, for ex reached goal.
 * A node is expandable if it represents a non-terminal stateNew and if it has unvisited children.
 *
 *                                   NodeInterface
 *                                /        |     \
 *      NodeWithChildrenInterface  NodeAbstract    \
 *            /                                    \
 *           /                                  /    \
 *   NodeNotTerminal            NodeTerminalFail   NodeTerminalNotFail  (all extends NodeAbstract)
 *
 */


public interface NodeInterface<SSV, AV> {

    String getName();

    ActionInterface<AV> getAction();

    StateInterface<SSV> getState();

    int getDepth();

    void setDepth(int depth);

    default Optional<NodeInterface<SSV, AV>> getChild(ActionInterface<AV> action)  {
        return Optional.empty();
    }

    default int nofChildNodes() {
        return 0;
    }

    default int getNofVisits() {
        return 0;
    }

    default List<NodeInterface<SSV, AV>> getChildNodes() {
        return new ArrayList<>();
    }

    default double getActionValue(ActionInterface<AV> a) {
        return 0;
    }

    void printTree();

    boolean isNotTerminal();

    boolean isTerminalFail();

    boolean isTerminalNoFail();


    static <SSV, AV> NodeNotTerminal<SSV, AV> newNotTerminal(StateInterface<SSV> s, ActionInterface<AV> action) {
        return new NodeNotTerminal<>(s, action);
    }


    static <SSV, AV> NodeTerminalFail<SSV, AV> newTerminalFail(StateInterface<SSV> s, ActionInterface<AV> action) {
        return new NodeTerminalFail<>(s, action);
    }

    static <SSV, AV> NodeTerminalNotFail<SSV, AV> newTerminalNotFail(StateInterface<SSV> s, ActionInterface<AV> action) {

        return new NodeTerminalNotFail<>(s, action);
    }

    static <SSV, AV> NodeInterface<SSV, AV> newNode(StepReturnGeneric<SSV> stepReturn, ActionInterface<AV> action) {
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
    static <SSV, AV> NodeInterface<SSV, AV> copy(NodeInterface<SSV, AV> otherNode) {
        if (otherNode instanceof NodeNotTerminal) {
            return new NodeNotTerminal<>((NodeNotTerminal<SSV, AV>) otherNode);
        } else if (otherNode instanceof NodeTerminalFail) {
            return new NodeTerminalFail<>((NodeTerminalFail<SSV, AV>) otherNode);
        } else {
            return new NodeTerminalNotFail<>((NodeTerminalNotFail<SSV, AV>) otherNode);
        }

    }

}
