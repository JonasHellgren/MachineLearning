package mcts_spacegame.models_mcts_nodes;

import common.MathUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.models_space.State;

import java.util.*;

@Log
public final class NodeNotTerminal extends NodeAbstract {

    private static final double INIT_ACTION_VALUE = 0d;
    private static final int INIT_NOF_VISITS = 0;
    List<NodeInterface> childNodes;
    int nofVisits;
    Map<ShipAction, Double> Qsa;
    Map<ShipAction, Integer> nSA;

    public NodeNotTerminal(State state, ShipAction action) {
        super(state,action);
        childNodes = new ArrayList<>();
        nofVisits = INIT_NOF_VISITS;
        Qsa = new HashMap<>();
        for (ShipAction a : ShipAction.applicableActions()) {
            Qsa.put(a, INIT_ACTION_VALUE);
        }

        nSA = new HashMap<>();
        for (ShipAction a : ShipAction.applicableActions()) {
            nSA.put(a, INIT_NOF_VISITS);
        }
    }

    public NodeNotTerminal(NodeNotTerminal node) {
        super(node.name,node.action,node.state,node.depth,node.actionRewardMap);
        this.childNodes=new ArrayList<>(node.childNodes);
        //childNodes.
        this.nofVisits=node.nofVisits;
        this.Qsa = new HashMap<>(node.Qsa);
        this.nSA = new HashMap<>(node.nSA);
    }

    @Override
    public void addChildNode(NodeInterface node) {
        childNodes.add(node);
        node.setDepth(depth + 1);
    }

    @Override
    public List<NodeInterface> getChildNodes() {
        return childNodes;
    }

    @Override
    public Optional<NodeInterface> getChild(ShipAction action) {
        List<NodeInterface> children= getChildNodes();
        return children.stream().filter(c -> c.getAction().equals(action)).findFirst();
    }

    @Override
    public int nofChildNodes() {
        return childNodes.size();
    }

    @Override
    public void printTree() {
        System.out.println(nameAndDepthAsString());
        childNodes.forEach(NodeInterface::printTree);
    }

    @Override
    public void increaseNofVisits() {
        nofVisits++;
    }

    @Override
    public void increaseNofActionSelections(ShipAction a) {
        int n = nSA.get(a);
        nSA.put(a, n + 1);
    }

    /***
     * Q (s,a) is the mean outcome of all simulations in which action a was selected in state s
     * https://www.lamsade.dauphine.fr/~cazenave/mcts-gelly-silver.pdf
     */

    @Override
    public void updateActionValue(double G, ShipAction a, double alpha) {
        int nofVisitsForAction=getNofActionSelections(a);
        if (MathUtils.isZero(nofVisitsForAction)) {
            throw new RuntimeException("Zero nof visits for action = " + a);
        }
        double qOld = getActionValue(a);
       // nofVisitsForAction=1;
        double qNew = qOld + alpha * (G - qOld) / (double) nofVisitsForAction;
        Qsa.put(a, qNew);
    }

    @Override
    public int getNofVisits() {
        return nofVisits;
    }

    @Override
    public int getNofActionSelections(ShipAction a) {
        return nSA.get(a);
    }

    @Override
    public double getActionValue(ShipAction a) {
        return Qsa.get(a);
    }


    @Override
    public String toString() {
        return super.toString() +
                ", state visits = " + nofVisits +
                ", values =" + Qsa.entrySet() +
                ", visits =" + nSA.entrySet()+
                ", rewards = "+actionRewardMap.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;  //check if the argument is a reference to this object
        if (!(obj instanceof NodeNotTerminal)) return false;  //check if correct typ
        return super.equals(obj);  //abstract class is annotated with @EqualsAndHashCode, fields checked
    }

}
