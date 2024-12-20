package monte_carlo_search.mcts_elevator;

import common.list_arrays.ListUtils;
import monte_carlo_tree_search.create_tree.MonteCarloSearchStatistics;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.elevator.ActionElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;

import java.util.List;
import java.util.stream.Collectors;

public class ElevatorTestHelper {

    NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot;
    MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator;
    MonteCarloSettings<VariablesElevator, Integer> settings;

    public ElevatorTestHelper(NodeWithChildrenInterface<VariablesElevator, Integer> nodeRoot,
                              MonteCarloTreeCreator<VariablesElevator, Integer> monteCarloTreeCreator,
                              MonteCarloSettings<VariablesElevator, Integer> settings) {
        this.nodeRoot = nodeRoot;
        this.monteCarloTreeCreator = monteCarloTreeCreator;
        this.settings=settings;
    }

    public void somePrinting() {
        System.out.println("nodeRoot = " + nodeRoot);
        List<Integer> speeds= getSpeeds();
        List<Integer> posList= getVisitedPositions();
        List<Integer> getnPersWaiting= getnPersWaiting();
        List<Double> soEListList= getSoEList();
        System.out.println("speeds = " + speeds);
        System.out.println("posList = " + posList);
        System.out.println("soEListList = " + soEListList);
        System.out.println("getnPersWaiting = " + getnPersWaiting);
        MonteCarloSearchStatistics<VariablesElevator,Integer> stats=monteCarloTreeCreator.getStatistics();
        System.out.println("stats = " + stats);
        ActionInterface<Integer> actionTemplate=  ActionElevator.newValueDefaultRange(0);
        System.out.println("nodeRoot value = " + NodeInfoHelper.valueNode(actionTemplate,nodeRoot));
        System.out.println("nodeRoot action values = " + NodeInfoHelper.actionValuesNode(actionTemplate, nodeRoot));
        System.out.println("valueNode = " + NodeInfoHelper.valueNode(actionTemplate, nodeRoot));
        System.out.println("monteCarloTreeCreator.getFirstAction() = " + monteCarloTreeCreator.getFirstAction());

    }

    List<Integer>  getnPersWaiting() {
        List<NodeInterface<VariablesElevator, Integer>> nodesOnPath = getNodesOnPath();
        return nodesOnPath.stream().map(n -> ListUtils.sumIntegerList(n.getState().getVariables().nPersonsWaiting)).collect(Collectors.toList());
    }

    List<Integer>  getSpeeds() {
        List<NodeInterface<VariablesElevator, Integer>> nodesOnPath = getNodesOnPath();
        return nodesOnPath.stream().map(n -> n.getState().getVariables().speed).collect(Collectors.toList());
    }

    List<Integer>  getVisitedPositions() {
        List<NodeInterface<VariablesElevator, Integer>> nodesOnPath = getNodesOnPath();
        return nodesOnPath.stream().map(n -> n.getState().getVariables().pos).collect(Collectors.toList());
    }

    List<Double>  getSoEList() {
        List<NodeInterface<VariablesElevator, Integer>> nodesOnPath = getNodesOnPath();
        return nodesOnPath.stream().map(n -> n.getState().getVariables().SoE).collect(Collectors.toList());
    }

    public List<NodeInterface<VariablesElevator, Integer>> getNodesOnPath() {
        TreeInfoHelper<VariablesElevator, Integer> tih=new TreeInfoHelper<>(nodeRoot,settings);
        return tih.getBestPath();
    }

}
