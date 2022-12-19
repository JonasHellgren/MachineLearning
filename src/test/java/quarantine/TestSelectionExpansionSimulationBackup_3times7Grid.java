package quarantine;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.environment.StepReturnREMOVE;
import mcts_spacegame.helpers.NodeInfoHelper;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.ActionSelector;
import mcts_spacegame.model_mcts.BackupModifier;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.NodeSelector;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

@Log
public class TestSelectionExpansionSimulationBackup_3times7Grid {

    private final double C_FOR_UCT = 1;

    private static final int DELTA_BIG = 2;
    private static final int NOF_ITERATIONS = 50;
    SpaceGrid spaceGrid;
    EnvironmentShip environment;
    NodeInterface nodeRoot;
    List<ShipAction> actionsToSelected;
    Optional<ShipAction> actionInSelected;
    StateShip startState;
    TreeInfoHelper tih;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new EnvironmentShip(spaceGrid);
    }

    private void initTree(StateShip state) {
        startState = state;
        nodeRoot = NodeInterface.newNotTerminal(startState, ShipAction.notApplicable);
        tih = new TreeInfoHelper(nodeRoot);
    }

    @Test
    public void oneIteration() {
        initTree(StateShip.newStateFromXY(0, 0));
        NodeInterface nodeSelected = select(nodeRoot);
        StepReturnGeneric<ShipVariables> sr = chooseActionAndExpand(nodeSelected);
        //todo simulation
        backPropagate(sr);

        System.out.println("sr = " + sr);
        nodeRoot.printTree();

        double valueUp = nodeRoot.getActionValue(ShipAction.up);
        System.out.println("nodeRoot = " + nodeRoot);
        System.out.println("valueDown = " + valueUp);
        Assert.assertEquals(-EnvironmentShip.MOVE_COST, valueUp, DELTA_BIG);
    }

    @Test
    public void iterateFromX0Y0() {
        initTree(StateShip.newStateFromXY(0, 0));
        doMCTSIterations();

        doPrinting(tih);

        Optional<NodeInterface> node11 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), StateShip.newStateFromXY(1, 1));
        Assert.assertFalse(node11.isEmpty());
        Optional<NodeInterface> node52 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), StateShip.newStateFromXY(5, 2));
        Assert.assertFalse(node52.isEmpty());
    }

    @Test
    public void iterateFromX0Y2() {
        initTree(StateShip.newStateFromXY(0, 2));
        doMCTSIterations();

        doPrinting(tih);

        Optional<NodeInterface> node12 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), StateShip.newStateFromXY(1, 2));
        Assert.assertFalse(node12.isEmpty());
        Optional<NodeInterface> node52 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), StateShip.newStateFromXY(5, 2));
        Assert.assertFalse(node52.isEmpty());
    }

    @Test(expected = InterruptedException.class)
    public void iterateFromX2Y0() {
        initTree(StateShip.newStateFromXY(2, 0));
        doMCTSIterations();

        doPrinting(tih);

        Optional<NodeInterface> node12 = NodeInfoHelper.findNodeMatchingState(tih.getBestPath(), StateShip.newStateFromXY(2, 0));
        Assert.assertTrue(node12.isPresent());
    }


    private void doPrinting(TreeInfoHelper tih) {
        System.out.println("nofNodesInTree = " + tih.nofNodesInTree());
        nodeRoot.printTree();
        tih.getBestPath().forEach(System.out::println);
    }

    private void doMCTSIterations() {
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            NodeInterface nodeSelected = select(nodeRoot);
            StepReturnGeneric<ShipVariables> sr = chooseActionAndExpand(nodeSelected);
            //todo simulation
            backPropagate(sr);
        }
    }

    @SneakyThrows
    private NodeInterface select(NodeInterface nodeRoot) {
        NodeSelector ns = new NodeSelector(nodeRoot, MonteCarloSettings.builder().build(), C_FOR_UCT,false);
        NodeInterface nodeSelected = ns.select();
        actionsToSelected = ns.getActionsFromRootToSelected();
        return nodeSelected;
    }

    @NotNull
    private StepReturnGeneric<ShipVariables> chooseActionAndExpand(NodeInterface nodeSelected) {
        StateShip state = TreeInfoHelper.getState(startState, environment, actionsToSelected);
        ActionSelector as = new ActionSelector(MonteCarloSettings.builder().build());
        actionInSelected = as.select(nodeSelected);
        NodeInterface child = null;
        StepReturnGeneric<ShipVariables> sr = null;
        if (actionInSelected.isPresent()) {
            sr = environment.step(actionInSelected.get(), state);
            nodeSelected.saveRewardForAction(actionInSelected.get(), sr.reward);
            child = NodeInterface.newNode(sr, actionInSelected.get());

            child.setDepth(nodeSelected.getDepth() + 1);  //easy to forget
            boolean isChildAddedEarlier = NodeInfoHelper.findNodeMatchingNode(nodeSelected.getChildNodes(), child).isPresent();

            if (isChildAddedEarlier) {
                log.warning("Child has been added earlier, child = " + child + ", in node = " + nodeSelected);
            }

            if (nodeSelected.isNotTerminal() && !isChildAddedEarlier) {
                nodeSelected.addChildNode(child);
            }
        }
        return sr;
    }

    @SneakyThrows
    private void backPropagate(StepReturnGeneric<ShipVariables> sr) {
        BackupModifier bum = BackupModifier.builder().rootTree(nodeRoot)
                .actionsToSelected(actionsToSelected)
                .actionOnSelected(actionInSelected.orElseThrow())
                .stepReturnOfSelected(sr)
                .build();
        bum.backup();
    }


}
