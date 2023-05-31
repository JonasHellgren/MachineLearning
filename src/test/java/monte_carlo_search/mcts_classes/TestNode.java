package monte_carlo_search.mcts_classes;

import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.search_tree_node_models.NodeInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNode {

    private static final double SIM_RES = -1d;
    ActionInterface<ShipActionSet> ACTION_ANY= ActionShip.newStill();
    MonteCarloSettings<ShipVariables,ShipActionSet> settings;

    @Before
    public void init() {
        settings= MonteCarloSettings.<ShipVariables,ShipActionSet>builder()
                .actionSelectionPolicy(new PolicyAlwaysStill())
                .simulationPolicy(new PolicyAlwaysStill())
                .build();
    }

    @Test public void whenDifferentTypesCreated_thenCorrect() {
        NodeInterface<ShipVariables,ShipActionSet> nodeWithChilds=NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> nodeTerminalFail=NodeInterface.newTerminalFail(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> nodeTerminalNoFail=NodeInterface.newTerminalNotFail(StateShip.newStateFromXY(0, 0),ACTION_ANY);

        Assert.assertTrue(nodeWithChilds.isNotTerminal());
        Assert.assertTrue(nodeTerminalFail.isTerminalFail());
        Assert.assertTrue(nodeTerminalNoFail.isTerminalNoFail());
    }

    @Test
    public void whenOneNode_thenNoChildNode() {
        NodeInterface<ShipVariables,ShipActionSet> node = NodeInterface.newTerminalFail(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        node.printTree();

        Assert.assertEquals(0, node.nofChildNodes());
    }

    @Test
    public void whenRootWithThreeChilds_thenCorrect() {
        NodeWithChildrenInterface<ShipVariables,ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chUp = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 1),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chStill = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chDown = NodeInterface.newTerminalFail(StateShip.newStateFromXY(1, 0),ACTION_ANY); //terminal

        nodeRoot.addChildNode(chUp);
        nodeRoot.addChildNode(chStill);
        nodeRoot.addChildNode(chDown);
        nodeRoot.printTree();

        TreeInfoHelper<ShipVariables,ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertEquals(4,tih.nofNodes() );
    }

    @Test
    public void whenRootWithGrandChilds_then6Nodes() {
        NodeWithChildrenInterface<ShipVariables,ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chUp1 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 1),ACTION_ANY);
        NodeWithChildrenInterface<ShipVariables,ShipActionSet> chStill1 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chDown1 = NodeInterface.newTerminalFail(StateShip.newStateFromXY(1, 0),ACTION_ANY); //terminal

        nodeRoot.addChildNode(chUp1);
        nodeRoot.addChildNode(chStill1);
        nodeRoot.addChildNode(chDown1);

        NodeInterface<ShipVariables,ShipActionSet> chUp2 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(2, 1),ACTION_ANY);
        NodeInterface<ShipVariables,ShipActionSet> chStill2 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(2, 0),ACTION_ANY);
        chStill1.addChildNode(chUp2);
        chStill1.addChildNode(chStill2);

        nodeRoot.printTree();


        TreeInfoHelper<ShipVariables,ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertEquals(6,tih.nofNodes() );
    }

    @Test
    public void whenImitateStillActionFromRootFollowedByExpansion_thenTwoNodes() {
        NodeWithChildrenInterface<ShipVariables,ShipActionSet> nodeRoot = NodeInterface.newNotTerminal(StateShip.newStateFromXY(0, 0), ActionShip.newNA());
        NodeInterface<ShipVariables,ShipActionSet> chStill1 = NodeInterface.newNotTerminal(StateShip.newStateFromXY(1, 0),ACTION_ANY);

        nodeRoot.addChildNode(chStill1);

        System.out.println("nodeRoot = " + nodeRoot);

        TreeInfoHelper<ShipVariables,ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        Assert.assertEquals(2,tih.nofNodes() );

    }

}
