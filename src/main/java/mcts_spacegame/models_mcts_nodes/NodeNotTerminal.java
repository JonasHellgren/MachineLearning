package mcts_spacegame.models_mcts_nodes;

import common.ConditionalUtils;
import common.MathUtils;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.Counter;
import mcts_spacegame.models_space.State;

import java.util.*;

@Log
public final class NodeNotTerminal extends NodeAbstract {

    private static final double INIT_ACTION_VALUE = 0d;
    private static final int INIT_NOF_VISITS = 0;
    List<NodeInterface> childNodes;
    int nofVisits;
    Map<Action, Double> Qsa;
    Map<Action, Integer> nSA;

    public NodeNotTerminal(State state, Action action) {
        super(state,action);
        childNodes = new ArrayList<>();
        nofVisits = INIT_NOF_VISITS;
        Qsa = new HashMap<>();
        for (Action a : Action.applicableActions()) {
            Qsa.put(a, INIT_ACTION_VALUE);
        }

        nSA = new HashMap<>();
        for (Action a : Action.applicableActions()) {
            nSA.put(a, INIT_NOF_VISITS);
        }

    }

    @Override
    protected void nofOffSpringsRec(NodeInterface node, Counter counter) {
        for (NodeInterface child : node.getChildNodes()) {
            counter.increment();
            nofOffSpringsRec(child, counter);
        }
    }

    @Override
    public void addChildNode(NodeInterface node) {
      //  ConditionalUtils.executeOnlyIfConditionIsTrue(childNodes.contains(node),
       //         () -> log.warning("Node already added"));

        childNodes.add(node);
        node.setDepth(depth + 1);
    }

    @Override
    public List<NodeInterface> getChildNodes() {
        return childNodes;
    }

    @Override
    public Optional<NodeInterface> getChild(Action action) {
        List<NodeInterface> children= getChildNodes();
        return children.stream().filter(c -> c.getAction().equals(action)).findFirst();
    }

    @Override
    public int nofChildNodes() {
        return childNodes.size();
    }

    @Override
    public int nofSubNodes() {
        counter = new Counter();
        nofOffSpringsRec(this, counter);
        return counter.value();
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
    public void increaseNofActionSelections(Action a) {
        int n = nSA.get(a);
        nSA.put(a, n + 1);
    }

    /***
     * Q (s,a) is the mean outcome of all simulations in which action a was selected in state s
     * https://www.lamsade.dauphine.fr/~cazenave/mcts-gelly-silver.pdf
     */

    @Override
    public void updateActionValue(double G, Action a,double alpha) {
        int nofVisitsForAction=getNofActionSelections(a);
        if (MathUtils.isZero(nofVisitsForAction)) {
            throw new RuntimeException("Zero nof visits for action = " + a);
        }
        double qOld = getActionValue(a);
        double qNew = qOld + alpha * (G - qOld) / (double) nofVisitsForAction;
        Qsa.put(a, qNew);
    }

    @Override
    public int getNofVisits() {
        return nofVisits;
    }

    @Override
    public int getNofActionSelections(Action a) {
        return nSA.get(a);
    }

    @Override
    public double getActionValue(Action a) {
        return Qsa.get(a);
    }

    @Override
    public Set<Action> getActionSet() {
        return Qsa.keySet();
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
