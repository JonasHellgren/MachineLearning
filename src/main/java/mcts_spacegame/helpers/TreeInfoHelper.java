package mcts_spacegame.helpers;

import lombok.Getter;
import lombok.Setter;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.model_mcts.NodeSelector;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class TreeInfoHelper {

    @Setter
    @Getter
    public class Counter {
        int count;
        public Counter( ) {
            count = 0;
        }

    }

    NodeInterface rootTree;

    public TreeInfoHelper(NodeInterface rootTree) {
        this.rootTree = rootTree;
    }


    //todo use getNodesVisitedForActions
    public Optional<NodeInterface> getNodeReachedForActions(List<Action> actions) {

        NodeInterface parent;
        parent = rootTree;
        for (Action action : actions) {
            Optional<NodeInterface> child = parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            parent = child.get();
        }

        return Optional.of(parent);
    }

    public Optional<List<NodeInterface>> getNodesOnPathForActions(List<Action> actionsToSelected) {

        NodeInterface parent;
        parent = rootTree;
        List<NodeInterface> nodes = new ArrayList<>();
        for (Action action : actionsToSelected) {
            Optional<NodeInterface> child = parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            nodes.add(parent);
            parent = child.get();
        }
        nodes.add(parent);

        return Optional.of(nodes);
    }

    public Optional<Double> getValueForActionInNode(List<Action> actionsToSelected, Action action) {
        Optional<NodeInterface> node = getNodeReachedForActions(actionsToSelected);

        return (node.isEmpty())
                ? Optional.empty()
                : Optional.of(node.get().getActionValue(action));

    }

    public static State getState(State rootState, Environment environment, List<Action> actionsToSelected) {
        State state = rootState.copy();
        for (Action a : actionsToSelected) {
            StepReturn sr = stepAndUpdateState(environment, state, a);
        }
        return state;
    }

    private static StepReturn stepAndUpdateState(Environment environment, State pos, Action a) {
        StepReturn sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }

    public List<NodeInterface> getBestPath() {
        NodeSelector ns = new NodeSelector(rootTree,true);
        ns.select();
        return ns.getNodesFromRootToSelected();
    }

    public int nofNodesInTree() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface,Integer> inc = (a,b) -> a+1;
        counter.setCount(1);  //don't forget grandma
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesWithNoChildren() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface,Integer> inc = (a,b) -> (b.nofChildNodes()==0) ? a+1:a;
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int maxDepth() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface,Integer> max = (a,b) -> Math.max(a,b.getDepth());
        evalRecursive(rootTree,counter,max);
        return counter.getCount();
    }

    public int totalNofChildren() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface,Integer> nofChilds = (a,b) -> a+b.nofChildNodes();
        counter.setCount(nofChilds.apply(counter.getCount(),rootTree)); //don't forget grandma
        evalRecursive(rootTree,counter,nofChilds);
        return counter.getCount();
    }

    private void evalRecursive(NodeInterface node, Counter counter, BiFunction<Integer,NodeInterface,Integer> bif) {
        for (NodeInterface  child:node.getChildNodes()) {
            counter.setCount(bif.apply(counter.getCount(),child));
            evalRecursive(child,counter,bif);
        }
    }


}
