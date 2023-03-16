package monte_carlo_tree_search.helpers;

import common.Conditionals;
import common.ListUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import monte_carlo_tree_search.classes.StepReturnGeneric;
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
public class TreeInfoHelper<SSV,AV> {

    private static final int C_FOR_NO_EXPLORATION = 0;

    @Setter
    @Getter
    public static class Counter {
        int count;
        public Counter( ) {
            count = 0;
        }
    }

    NodeWithChildrenInterface<SSV,AV> rootTree;
    MonteCarloSettings<SSV,AV> settings;

    public TreeInfoHelper(NodeWithChildrenInterface <SSV,AV> rootTree, MonteCarloSettings<SSV,AV> settings) {
        this.rootTree = rootTree;
        this.settings=settings;
    }

    public Optional<NodeInterface <SSV,AV>> getNodeReachedForActions(List<ActionInterface<AV>> actions) {
        Optional<List<NodeInterface <SSV,AV>>> nodes=getNodesOnPathForActions(actions);
        return  (nodes.isEmpty())
                ?Optional.empty()
                :Optional.of(nodes.get().get(nodes.get().size()-1));
    }

    public Optional<List<NodeInterface <SSV,AV>>> getNodesOnPathForActions(List<ActionInterface<AV>> actionsToSelected) {

        NodeInterface<SSV,AV> parent = rootTree;
        List<NodeInterface <SSV,AV>> nodes = new ArrayList<>();
        for (ActionInterface<AV> action : actionsToSelected) {
            Optional<NodeInterface <SSV,AV>> child = parent.getChild(action);
            if (child.isEmpty()) {
                return Optional.empty();
            }
            nodes.add(parent);
            parent =  child.get();  //todo clean?
        }
        nodes.add(parent);
        return Optional.of(nodes);
    }

    public Optional<Double> getValueForActionInNode(List<ActionInterface<AV>> actionsToSelected, ActionInterface<AV> action) {
        Optional<NodeInterface <SSV,AV>> node =  getNodeReachedForActions(actionsToSelected);


        return (node.isEmpty())
                ? Optional.empty()
                : Optional.of(node.get().getActionValue(action));

       /* return (node.isEmpty())
                ? Optional.empty()
                : Optional.of(node.get().getActionValue(action));  */
    }

    public static <SSV, AV> StateInterface<SSV> getState(StateInterface<SSV> rootState,
                                                         EnvironmentGenericInterface<SSV, AV> environment,
                                                         List<ActionInterface<AV>> actionsToSelected) {
        StateInterface<SSV> state = rootState.copy();
        for (ActionInterface<AV> a : actionsToSelected) {
            StepReturnGeneric<SSV> sr = environment.step(a, state);
            state.setFromReturn(sr);
        }
        return state;
    }

    @SneakyThrows
    public List<NodeInterface <SSV,AV>> getBestPath() {  //todo innehåller ej  bestChild
        NodeSelector<SSV,AV> ns = new NodeSelector<>(rootTree, settings, C_FOR_NO_EXPLORATION);
        ns.select();
        return ns.getNodesFromRootToSelected();
    }

    @SneakyThrows
    public List<ActionInterface <AV>> getActionsOnBestPath() {
        NodeSelector<SSV,AV> ns = new NodeSelector<>(rootTree, settings, C_FOR_NO_EXPLORATION);
        NodeWithChildrenInterface<SSV,AV> nodeSelected= ns.select();
        List<ActionInterface <AV>> actionsToSelected=ns.getActionsFromRootToSelected();
        Optional<NodeInterface<SSV,AV>> bestChild= ns.selectNonFailChildWithHighestUCT(nodeSelected);
        return  (bestChild.isEmpty())
                ? actionsToSelected
                : ListUtils.merge(actionsToSelected, Collections.singletonList(bestChild.orElseThrow().getAction()));
    }


    public Optional<AV> getValueOfFirstBestAction() {  //todo remove - use NodeInfo instead
        List<ActionInterface <AV>> actionList = getActionsOnBestPath();
        if (actionList.isEmpty()) {
            throw new RuntimeException("Empty action list");
         //   log.warning("Empty action list");
         //   return Optional.empty();
        }
        return Optional.of(actionList.get(0).getValue());

    }

    public int nofNodes() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> inc = (a,b) -> a+1;
        counter.setCount(1);  //don't forget root
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesNotTerminal() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> inc = (a,b) ->
                a+(b.isNotTerminal() ? 1:0);
        counter.setCount(1);  //don't forget root
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesFail() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> inc = (a,b) ->
                a+(b.isTerminalFail() ? 1:0);
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesWithNoChildren() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> inc = (a,b) -> (b.nofChildNodes()==0) ? a+1:a;
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int nofNodesAtDepth(int depth) {
        if (depth==0) {  //don't forget root
            return 1;
        }
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> inc = (a,b) -> a+((b.getDepth()==depth) ?1:0);
        evalRecursive(rootTree,counter,inc);
        return counter.getCount();
    }

    public int maxDepth() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> max = (a,b) -> Math.max(a,b.getDepth());
        evalRecursive(rootTree,counter,max);
        return counter.getCount();
    }

    public int totalNofChildren() {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> nofChilds = (a,b) -> a+b.nofChildNodes();
        counter.setCount(nofChilds.apply(counter.getCount(),rootTree)); //don't forget grandma
        evalRecursive(rootTree,counter,nofChilds);
        return counter.getCount();
    }

    //todo generic
    public boolean isStateInAnyNode(StateShip state) {
        Counter counter = new Counter();
        BiFunction<Integer,NodeInterface <SSV,AV>,Integer> nofChildrenThatEqualsState =
                (a,b) -> a+(b.getState().getVariables().equals(state.getVariables())?1:0);
        counter.setCount(nofChildrenThatEqualsState.apply(counter.getCount(),rootTree)); //don't forget grandma
        evalRecursive(rootTree,counter,nofChildrenThatEqualsState);

        Conditionals.executeIfTrue(counter.getCount()>1, () ->
            log.warning("More than one node has state = "+state));

        return counter.getCount()>0;
    }

    public boolean isOnBestPath(StateInterface<SSV> state) {
        Optional<NodeInterface<SSV,AV>> node =
                NodeInfoHelper.findNodeMatchingStateVariables(getBestPath(), state);
        return node.isPresent();
    }

    private void evalRecursive(NodeInterface <SSV,AV> node, Counter counter, BiFunction<Integer,NodeInterface <SSV,AV>,Integer> bif) {
        for (NodeInterface <SSV,AV>  child:node.getChildNodes()) {
            counter.setCount(bif.apply(counter.getCount(),child));
            evalRecursive(child,counter,bif);
        }
    }
}
