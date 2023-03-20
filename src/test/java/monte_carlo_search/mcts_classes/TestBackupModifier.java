package monte_carlo_search.mcts_classes;

import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.BackupModifier;
import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.models_and_support_classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.helpers.TreeInfoHelper;
import monte_carlo_tree_search.node_models.NodeInterface;
import monte_carlo_tree_search.node_models.NodeWithChildrenInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestBackupModifier {

    private static final int MAX_NOF_ITERATIONS = 15;
    MonteCarloTreeCreator<ShipVariables, ShipActionSet> monteCarloTreeCreator;
    EnvironmentGenericInterface<ShipVariables, ShipActionSet> environment;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;
    ActionInterface<ShipActionSet> actionTemplate;
    NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot;
    TreeInfoHelper<ShipVariables, ShipActionSet> tih;
    List<ActionInterface<ShipActionSet>> actionsToX2Y0;
    List<ActionInterface<ShipActionSet>> actionsToX5Y2;


    @SneakyThrows
    @Before
    public void init() {
        SpaceGrid spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
        actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action
        settings = MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .maxNofIterations(MAX_NOF_ITERATIONS)
                .build();
        monteCarloTreeCreator= MonteCarloTreeCreator.<ShipVariables, ShipActionSet>builder()
                .environment(environment)
                .startState(StateShip.newStateFromXY(0,0))
                .monteCarloSettings(settings)
                .actionTemplate(actionTemplate)
                .build();
        nodeRoot=monteCarloTreeCreator.run();
        tih=new TreeInfoHelper<>(nodeRoot,settings);

        actionsToX2Y0 = Arrays.asList(ActionShip.newUp(), ActionShip.newDown());
        actionsToX5Y2 = Arrays.asList(ActionShip.newUp(), ActionShip.newUp(), ActionShip.newStill(), ActionShip.newStill(), ActionShip.newStill());

    }

    @Test
    public void givenIterations_thenTreeSizeIsNofIterationsPlus1() {
        System.out.println("tih.nofNodes() = " + tih.nofNodes());
        Assert.assertEquals(MAX_NOF_ITERATIONS+1,tih.nofNodes());
    }


    @Test
    public void givenIterations_thenTreeIncludesX2Y0AndX5Y2() {
        System.out.println("tih.nofNodes() = " + tih.nofNodes());
        nodeRoot.printTree();
        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(2,0)));
        Assert.assertTrue(tih.isStateInAnyNode(StateShip.newStateFromXY(5,2)));
    }

    @Test
    public void givenBackupInX2Y0_thenNodeRootUpdatedActionValueUp() {
        double valueUpBefore= nodeRoot.getActionValue(ActionShip.newUp());
        doBackup(actionsToX2Y0,ActionShip.newStill(),nodeRoot);
        double valueUpAfter= nodeRoot.getActionValue(ActionShip.newUp());
        System.out.println("valueUpBefore = " + valueUpBefore+", valueUpAfter = " + valueUpAfter);
        Assert.assertTrue(valueUpAfter<valueUpBefore);
    }

    @Test
    public void givenBackupInX5Y2_thenNodeRootUpdatedActionValueUp() {
        double valueUpBefore= nodeRoot.getActionValue(ActionShip.newUp());
        doBackup(actionsToX5Y2,ActionShip.newStill(),nodeRoot);
        double valueUpAfter= nodeRoot.getActionValue(ActionShip.newUp());
        System.out.println("valueUpBefore = " + valueUpBefore+", valueUpAfter = " + valueUpAfter);
        Assert.assertTrue(valueUpAfter>valueUpBefore);
    }

    @Test
    public void givenBackupInX2Y0_thenDefensiveBackupGivesSmallerChangeInNodeRootValueUp() {
        double changeNoDefensive=calcChange();
        settings.setDiscountFactorDefensiveSteps(0.9);
        settings.setWeightReturnsSteps(0.9);
        double changeDefensive=calcChange();
        System.out.println("changeNoDefensive = " + changeNoDefensive+", changeDefensive = " + changeDefensive);
        Assert.assertTrue(changeDefensive<changeNoDefensive);
    }

    private double calcChange() {
        double valueUpBefore= nodeRoot.getActionValue(ActionShip.newUp());
        doBackup(actionsToX2Y0,ActionShip.newStill(),nodeRoot);
        double valueUpAfter= nodeRoot.getActionValue(ActionShip.newUp());
        return Math.abs(valueUpAfter-valueUpBefore);
    }

    private void doBackup(List<ActionInterface<ShipActionSet>> actionsToSelected,
                          ActionInterface<ShipActionSet> actionInSelected,
                          NodeWithChildrenInterface<ShipVariables, ShipActionSet> nodeRoot) {

        Optional<NodeInterface<ShipVariables, ShipActionSet>> node=tih.getNodeReachedForActions(actionsToSelected);
        StepReturnGeneric<ShipVariables> sr=environment.step(actionInSelected, node.orElseThrow().getState());

        BackupModifier<ShipVariables, ShipActionSet> bum = BackupModifier.<ShipVariables, ShipActionSet>builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected)
                .stepReturnOfSelected(sr)
                .settings(settings)
                .memoryValueStateAfterAction(0)
                .build();
        bum.backup();
    }

}
