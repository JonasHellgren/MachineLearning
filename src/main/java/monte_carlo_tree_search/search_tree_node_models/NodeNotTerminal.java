package monte_carlo_tree_search.search_tree_node_models;

import common.other.Conditionals;
import common.math.MathUtils;
import lombok.extern.java.Log;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.*;

@Log
public final class NodeNotTerminal<SSV,AV>
        extends NodeAbstract<SSV,AV> implements NodeWithChildrenInterface<SSV,AV> {
    private static final double INIT_REWARD_VALUE = 0d;
    private static final double INIT_ACTION_VALUE = 0d;
    private static final int INIT_NOF_VISITS = 0;
    List<NodeInterface<SSV,AV>> childNodes;
    int nofVisits;
    Map<AV, Double> qSA;
    Map<AV, Integer> nSA;
    Map<AV, Double> actionRewardMap;

    public NodeNotTerminal(StateInterface<SSV> state, ActionInterface<AV> action) {
        super(state,action);
        childNodes = new ArrayList<>();
        nofVisits = INIT_NOF_VISITS;
        qSA = new HashMap<>();
        Set<AV> actionValues=  action.applicableActions();
        for (AV av : actionValues) {
            qSA.put(av, INIT_ACTION_VALUE);
        }

        nSA = new HashMap<>();
        for (AV av : actionValues) {
            nSA.put(av, INIT_NOF_VISITS);
        }
        this.actionRewardMap=new HashMap<>();
    }

    public NodeNotTerminal(NodeNotTerminal<SSV,AV> node) {
        super(node.name,node.action,node.state,node.depth);
        this.childNodes=new ArrayList<>(node.childNodes);
        //childNodes.
        this.nofVisits=node.nofVisits;
        this.qSA = new HashMap<>(node.qSA);
        this.nSA = new HashMap<>(node.nSA);
        this.actionRewardMap=node.actionRewardMap;
    }

    public void saveRewardForAction(ActionInterface<AV> action, double reward) {  //till NodeNotTerminal
        Conditionals.executeIfTrue(actionRewardMap.containsKey(action.getValue()),
                () -> log.fine("Reward for action already defined"));

        actionRewardMap.put(action.getValue(),reward);
    }

    public double restoreRewardForAction(ActionInterface<AV> action) {  //till NodeNotTerminal
        return actionRewardMap.getOrDefault(action.getValue(),INIT_REWARD_VALUE);
    }

    @Override
    public void addChildNode(NodeInterface<SSV,AV> node) {
        childNodes.add(node);
        node.setDepth(depth + 1);
    }

 //   @Override
    public List<NodeInterface<SSV,AV>> getChildNodes() {
        return childNodes;
    }


   @Override
    public Optional<NodeInterface<SSV, AV>> getChild(ActionInterface<AV> action) {
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
    public void updateActionValue(double G, ActionInterface<AV> a, double alpha, double nofVisitsExponent) {
        int nofVisitsForAction=getNofActionSelections(a);
        if (MathUtils.isZero(nofVisitsForAction)) {
            throw new RuntimeException("Zero nof visits for action = " + a);
        }
        double qOld = getActionValue(a);

       // System.out.println("qOld = " + qOld+", G = " + G);
        double qNew = qOld + alpha * (G - qOld) / (double) Math.pow(nofVisitsForAction,nofVisitsExponent);
        qSA.put(a.getValue(), qNew);
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
        return qSA.get(a.getValue());
    }


    @Override
    public String toString() {
        return super.toString() +
                ", state visits = " + nofVisits +
                ", values =" + qSA.entrySet() +
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
