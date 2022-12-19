package mcts_spacegame.models_mcts_nodes;

import common.MathUtils;
import lombok.extern.java.Log;
import mcts_spacegame.generic_interfaces.ActionInterface;
import mcts_spacegame.generic_interfaces.StateInterface;

import java.util.*;

@Log
public final class NodeNotTerminal<SSV,AV> extends NodeAbstract<SSV,AV> {

    private static final double INIT_ACTION_VALUE = 0d;
    private static final int INIT_NOF_VISITS = 0;
    List<NodeInterface<SSV,AV>> childNodes;
    int nofVisits;
    Map<AV, Double> Qsa;
    Map<AV, Integer> nSA;

    public NodeNotTerminal(StateInterface<SSV> state, ActionInterface<AV> action) {
        super(state,action);
        childNodes = new ArrayList<>();
        nofVisits = INIT_NOF_VISITS;
        Qsa = new HashMap<>();
        Set<AV> actionValues=  action.applicableActions();
        for (AV av : actionValues) {
            Qsa.put(av, INIT_ACTION_VALUE);
        }

        nSA = new HashMap<>();
        for (AV av : actionValues) {
            nSA.put(av, INIT_NOF_VISITS);
        }
    }

    public NodeNotTerminal(NodeNotTerminal<SSV,AV> node) {
        super(node.name,node.action,node.state,node.depth,node.actionRewardMap);
        this.childNodes=new ArrayList<>(node.childNodes);
        //childNodes.
        this.nofVisits=node.nofVisits;
        this.Qsa = new HashMap<>(node.Qsa);
        this.nSA = new HashMap<>(node.nSA);
    }

    @Override
    public void addChildNode(NodeInterface<SSV,AV> node) {
        childNodes.add(node);
        node.setDepth(depth + 1);
    }

    @Override
    public List<NodeInterface<SSV,AV>> getChildNodes() {
        return childNodes;
    }

    @Override
    public Optional<NodeInterface<SSV,AV>> getChild(ActionInterface<AV> action) {
        List<NodeInterface<SSV,AV>> children= getChildNodes();
        return children.stream()
                .filter(c -> c.getAction().getValue().equals(action.getValue()))
                .findFirst();
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
    public void increaseNofActionSelections(ActionInterface<AV> a) {
        int n = getNofActionSelections(a);
        nSA.put(a.getValue(), n + 1);
    }

    /***
     * Q (s,a) is the mean outcome of all simulations in which action a was selected in state s
     * https://www.lamsade.dauphine.fr/~cazenave/mcts-gelly-silver.pdf
     */

    @Override
    public void updateActionValue(double G, ActionInterface<AV> a, double alpha) {
        int nofVisitsForAction=getNofActionSelections(a);
        if (MathUtils.isZero(nofVisitsForAction)) {
            throw new RuntimeException("Zero nof visits for action = " + a);
        }
        double qOld = getActionValue(a);
       // nofVisitsForAction=1;
        double qNew = qOld + alpha * (G - qOld) / (double) nofVisitsForAction;
        Qsa.put(a.getValue(), qNew);
    }

    @Override
    public int getNofVisits() {
        return nofVisits;
    }

    @Override
    public int getNofActionSelections(ActionInterface<AV> a) {
        return nSA.get(a.getValue());
    }

    @Override
    public double getActionValue(ActionInterface<AV> a) {
        return Qsa.get(a.getValue());
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
