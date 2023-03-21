package monte_carlo_tree_search.helpers;

import common.Conditionals;
import common.ListUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.NodeSelector;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.domains.models_space.StateShip;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.*;
import java.util.function.BiFunction;

/***
 * Extracts info from monte carlo decision tree
 */

@Log
@Getter
public class TreeInfoHelper<S, A> {

    private static final int C_FOR_NO_EXPLORATION = 0;

    @Setter
    @Getter
    public static class Counter {
        int count;
        public Counter( ) {
            count = 0;
        }
    }

    NodeWithChildrenInterface<S, A> rootTree;
    MonteCarloSettings<S, A> settings;

    public TreeInfoHelper(NodeWithChildrenInterface <S, A> rootTree, MonteCarloSettings<S, A> settings) {
        this.rootTree = rootTree;
        this.settings=settings;
    }

    public Optional<NodeInterface <S, A>> getNodeReachedForActions(List<ActionInterface<A>> actions) {
        Optional<List<NodeInterface <S, A>>> nodes=getNodesOnPathForActions(actions);
        return  (nodes.isEmpty())
                ?Optional.empty()
                :Optional.of(nodes.get().get(nodes.get().size()-1));
    }

    public Optional<List<NodeInterface <S, A>>> getNodesOnPathForActions(List<ActionInterface<A>> actionsToSelected) {
        NodeInterface<S, A> parent = rootTree;
        List<NodeInterface <S, A>> nodes = new ArrayList<>();
        for (ActionInterface<A> action : actionsToSelected) {
            Optional<NodeInterface <S, A>> child = parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            nodes.add(parent);
            parent =  child.get();  //todo clean?
        }
        nodes.add(parent);
        return Optional.of(nodes);
    }

    public Optional<Double> getValueForActionInNode(List<ActionInterface<A>> actionsToSelected,
                                                    ActionInterface<A> action) {
        Optional<NodeInterface <S, A>> node =  getNodeReachedForActions(actionsToSelected);

        return (node.isEmpty())
                ? Optional.empty()
                : Optional.of(node.get().getActionValue(action));

    }

    public static <S, A> StateInterface<S> getState(StateInterface<S> rootState,
                                                    EnvironmentGenericInterface<S, A> environment,
                                                    List<ActionInterface<A>> actionsToSelected) {
        StateInterface<S> state = rootState.copy();
        for (ActionInterface<A> a : actionsToSelected) {
            StepReturnGeneric<S> sr = environment.step(a, state);
            state.setFromReturn(sr);
        }
        return state;
    }

    @SneakyThrows
    public List<NodeInterface <S, A>> getBestPath() {
        NodeSelector<S, A> ns = new NodeSelector<>(rootTree, settings, C_FOR_NO_EXPLORATION);
        ns.select();
        return ns.getNodesFromRootToSelected();
    }

    @SneakyThrows
    public List<ActionInterface <A>> getActionsOnBestPath() {
        NodeSelector<S, A> ns = new NodeSelector<>(rootTree, settings, C_FOR_NO_EXPLORATION);
        NodeWithChildrenInterface<S, A> nodeSelected= ns.select();
        List<ActionInterface <A>> actionsToSelected=ns.getActionsFromRootToSelected();
        Optional<NodeInterface<S, A>> bestChild= ns.selectNonFailChildWithHighestUCT(nodeSelected);
        return  (bestChild.isEmpty())
                ? actionsToSelected
                : ListUtils.merge(actionsToSelected, Collections.singletonList(bestChild.orElseThrow().getAction()));
    }


    public int nofNodes() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> inc = (a, b) -> a+1;
        counter.setCount(1);  //don't forget root
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesNotTerminal() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> inc = (a, b) ->
                a+(b.isNotTerminal() ? 1:0);
        counter.setCount(1);  //don't forget root
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesFail() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> inc = (a, b) ->
                a+(b.isTerminalFail() ? 1:0);
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesWithNoChildren() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> inc = (a, b) -> (b.nofChildNodes()==0) ? a+1:a;
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesAtDepth(int depth) {
        if (depth==0) {  //don't forget root
            return 1;
        }
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> inc = (a, b) -> a+((b.getDepth()==depth) ?1:0);
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int maxDepth() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> max = (a, b) -> Math.max(a,b.getDepth());
        evalRecursive(rootTree,counter,max);
        return counter.getCount();
    }

    public int totalNofChildren() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> nofChilds = (a, b) -> a+b.nofChildNodes();
        counter.setCount(nofChilds.apply(counter.getCount(),rootTree)); //don't forget grandma
        evalRecursive(rootTree,counter,nofChilds);
        return counter.getCount();
    }

    public boolean isStateInAnyNode(StateInterface<S> state) {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <S, A>,Integer> nofChildrenThatEqualsState =
                (a,b) -> a+(b.getState().getVariables().equals(state.getVariables())?1:0);
        counter.setCount(nofChildrenThatEqualsState.apply(counter.getCount(),rootTree)); //don't forget grandma
        evalRecursive(rootTree,counter,nofChildrenThatEqualsState);

        Conditionals.executeIfTrue(counter.getCount()>1, () ->
            log.warning("More than one node has state = "+state));

        return counter.getCount()>0;
    }

    public boolean isOnBestPath(StateInterface<S> state) {
        Optional<NodeInterface<S, A>> node =
                NodeInfoHelper.findNodeMatchingStateVariables(getBestPath(), state);
        return node.isPresent();
    }

    private void evalRecursive(NodeInterface <S, A> node, Counter counter, BiFunction<Integer,NodeInterface <S, A>,Integer> bif) {
        for (NodeInterface <S, A>  child:node.getChildNodes()) {
            counter.setCount(bif.apply(counter.getCount(),child));
            evalRecursive(child,counter,bif);
        }
    }
}
