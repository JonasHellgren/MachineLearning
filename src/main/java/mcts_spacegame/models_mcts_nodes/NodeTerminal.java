package mcts_spacegame.models_mcts_nodes;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.generic_interfaces.StateInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.StateShip;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Log
public abstract class NodeTerminal extends NodeAbstract {  //todo TerminalLeaf

    public NodeTerminal(StateInterface<ShipVariables> state, ShipAction action) {
        super(state,action);
    }

    public NodeTerminal(NodeTerminal node) {
        super(node.name,node.action,node.state,node.depth,node.actionRewardMap);
    }

    @Override
    @SneakyThrows
    public void addChildNode(NodeInterface node) {
        log.warning("Can't add child to node without child");
    }

    @Override
    public List<NodeInterface> getChildNodes() {
        return Collections.emptyList();
    }

    @Override
    public Optional<NodeInterface> getChild(ShipAction action) {
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
    public void increaseNofActionSelections(ShipAction a) {

    }

    @Override
    public void updateActionValue(double G, ShipAction a, double alpha) {

    }

    @Override
    public int getNofVisits() {
        return 0;
    }

    @Override
    public int getNofActionSelections(ShipAction a) {
        return 0;
    }

    @Override
    public double getActionValue(ShipAction a) {
        return 0;
    }


}
