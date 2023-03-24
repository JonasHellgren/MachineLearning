package monte_carlo_search.mcts_spacegame;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.helpers.NodeInfoHelper;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.search_tree_node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * This tests shows the power of defensive backup
 * With a large exploration factor and large (absolute) reward for fail states backups can be miss-leading
 * In this environment path leading to +10 goal is best but path at y=0 is trap hence down action in start state
 * is misinterpreted as bad without defensive backup
 */

public class Test_5times7GridWithTrapCorridor {

    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;

    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new5times7GridWithTrapCorridor();

        System.out.println("spaceGrid = " + spaceGrid);

        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action

        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .minNofIterations(1_000).maxNofIterations(2_000).timeBudgetMilliSeconds(200)
                .coefficientExploitationExploration(1e2)
                .alphaBackupNormal(1.0).discountFactorSteps(1.0)
                .isDefensiveBackup(true).alphaBackupDefensiveStep(0.01).discountFactorDefensiveSteps(0.1)
                .build();
        monteCarloTreeCreator= MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,2))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
    }

    @SneakyThrows
    @Test
    public void givenNoDefensiveBackup_whenStartingFrommX0Y2_thenX3Y2IsNotOnBestPath() {
        settings.setDefensiveBackup(false);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        somePrinting(nodeRoot, tih);
        Assert.assertFalse(SpaceGameTestHelper.isOnBestPath(3,2,tih));
    }

    @SneakyThrows
    @Test
    public void givenDefensiveBackup_whenStartingFrommX0Y2_thenX3Y2IsOnBestPath() {
        settings.setDefensiveBackup(true);
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot=monteCarloTreeCreator.run();
        TreeInfoHelper<ShipVariables, ShipActionSet> tih=new TreeInfoHelper<>(nodeRoot,settings);
        somePrinting(nodeRoot, tih);
        Assert.assertTrue(SpaceGameTestHelper.isOnBestPath(3,2,tih));
    }

    private void somePrinting(NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot, TreeInfoHelper<ShipVariables, ShipActionSet> tih) {
        System.out.println("actionValueMap nodeRoot = " + NodeInfoHelper.actionValueMap(actionTemplate, nodeRoot));
        NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeX2Y1=
                (NodeWithChildrenInterface<ShipVariables, ShipActionSet>) tih.getNodeReachedForActions(
                Arrays.asList(ActionShip.newDown(),ActionShip.newStill())).orElseThrow();
        System.out.println("actionValueMap nodeX2Y1 = " + NodeInfoHelper.actionValueMap(actionTemplate, nodeX2Y1));

        tih.getBestPath().forEach(n -> System.out.println(n.getState().getVariables()));

    }



}
