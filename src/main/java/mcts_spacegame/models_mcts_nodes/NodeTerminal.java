package mcts_spacegame.models_mcts_nodes;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipActionSet;
import mcts_spacegame.models_space.ShipVariables;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log
public abstract class NodeTerminal<SSV,AV> extends NodeAbstract<SSV,AV> {  //todo TerminalLeaf

    public NodeTerminal(StateInterface<SSV> state, ActionInterface<AV> action) {
        super(state,action);
    }

    public NodeTerminal(NodeTerminal<SSV,AV> node) {
        super(node.name,node.action,node.state,node.depth,node.actionRewardMap);
    }

    @Override
    @SneakyThrows
    public void addChildNode(NodeInterface<SSV,AV> node) {
        log.warning("Can't add child to node without child");
    }

    @Override
    public List<NodeInterface<SSV,AV>> getChildNodes() {
        return Collections.emptyList();
    }

    @Override
    public Optional<NodeInterface<SSV,AV>> getChild(ActionInterface<AV> action) {
        return Optional.empty();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int nofChildNodes() {
        return 0;
    }

    @Override
    public void increaseNofVisits() {

    }

    @Override
    public void increaseNofActionSelections(ActionInterface<AV> a) {

    }

    @Override
    public void updateActionValue(double G, ActionInterface<AV> a, double alpha) {

    }

    @Override
    public int getNofVisits() {
        return 0;
    }

    @Override
    public int getNofActionSelections(ActionInterface<AV> a) {
        return 0;
    }

    @Override
    public double getActionValue(ActionInterface<AV> a) {
        return 0;
    }


}
