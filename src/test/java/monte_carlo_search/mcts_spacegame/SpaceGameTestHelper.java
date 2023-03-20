package monte_carlo_search.mcts_spacegame;

import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.models_space.ActionShip;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.domains.models_space.StateShip;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;

import java.util.Optional;

public class SpaceGameTestHelper {

    public static void doPrinting(TreeInfoHelper<ShipVariables, ShipActionSet> tih,
                            NodeInterface<ShipVariables, ShipActionSet> nodeRoot) {


        System.out.println("nofNodesInTree = " + tih.nofNodes());
        nodeRoot.printTree();
        tih.getBestPath().forEach((n) -> System.out.println(n.getState().getVariables()));
    }

    public static void doRootNodePrinting(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot) {
        double valueUp= nodeRoot.getActionValue(ActionShip.newUp());
        double valueStill= nodeRoot.getActionValue(ActionShip.newStill());
        double valueDown= nodeRoot.getActionValue(ActionShip.newDown());
        System.out.println("valueUp = " + valueUp+", valueStill = " + valueStill+", valueDown = " + valueDown);
    }

    public static void doPrinting(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot,
                            MonteCarloSettings<ShipVariables, ShipActionSet> settings,
                                  MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator) {
        TreeInfoHelper<ShipVariables, ShipActionSet> tih = new TreeInfoHelper<>(nodeRoot, settings);

        System.out.println("nofNodesInTree = " + tih.nofNodes());
        System.out.println("monteCarloTreeCreator.getStatistics() = " + monteCarloTreeCreator.getStatistics());
        System.out.println("nodeRoot = " + nodeRoot);
        tih.getBestPath().forEach(n -> System.out.println(n.getState().getVariables()));
    }

    public static boolean isOnBestPath(int x, int y, TreeInfoHelper<ShipVariables, ShipActionSet> tih) {
        Optional<NodeInterface<ShipVariables, ShipActionSet>> node11 =
                NodeInfoHelper.findNodeMatchingStateVariables(tih.getBestPath(), StateShip.newStateFromXY(x,y));
        return node11.isPresent();
    }

}
