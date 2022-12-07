package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.Environment;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.model_mcts.BackupModifierFromSimulations;
import mcts_spacegame.model_mcts.BackupModifierFromSteps;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.SimulationResults;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.State;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBackupModifierFromSimulations {

    private static final double DELTA = 0.1;
    private static final int DELTA_BIG = 2;
    private static final Action ACTION_ON_SELECTED = Action.still;
    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 0.9;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final double ALPHA_BACKUP_SIMULATION_DEFENSIVE = 0.1;
    private static final int ALPHA_BACKUP_SIMULATION_NORMAL = 1;
    List<Action> actions = Arrays.asList(Action.up, Action.up);

    SpaceGrid spaceGrid;
    Environment environment;
    BackupModifierFromSteps bum;
    List<StepReturn> stepReturns;
    NodeInterface treeRoot;
    TreeInfoHelper treeInfoHelper;
    SimulationResults simulationResults;
    MonteCarloSettings settings;
    // StepReturn getStepReturnOfSelected;

    @Before
    public void init() {
        spaceGrid = SpaceGridInterface.new3times7Grid();
        environment = new Environment(spaceGrid);
        stepReturns = new ArrayList<>();
        State rootState = new State(0, 0);

        treeRoot = createMCTSTree(actions, rootState, stepReturns);
        treeInfoHelper=new TreeInfoHelper(treeRoot);
        simulationResults = new SimulationResults();
        settings= MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1)  //max return
                .discountFactorSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE).build();
    }

    @Test
    public void showTree() {
        treeRoot.printTree();
        System.out.println("treeInfoHelper.getBestPath() = " + treeInfoHelper.getBestPath());
        printNodesOnPath();
    }

    @Test public void backUpOneSimulationResult() {
        double g=1;
        simulationResults.add(g, false);

        BackupModifierFromSimulations bms = getBackupModifierFromSimulations();
        List<Double> values=bms.backup();

//        List<Double> values=getActionValuesOnPath();
  //      printNodesOnPath();
        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g,values.get(values.size()-1), DELTA),
                () -> assertEquals(g*DISCOUNT_FACTOR_SIMULATION_NORMAL,values.get(values.size()-2), DELTA),
                () -> assertEquals(g*Math.pow(DISCOUNT_FACTOR_SIMULATION_NORMAL,2),values.get(values.size()-3), DELTA)
        );
    }


    @Test public void backUpTwoSimulationsResults() {
        double g1=1, g2=-1;  //only g1 is backed up due to coefficientMaxAverageReturn(1)
        simulationResults.add(g1, false);
        simulationResults.add(g2, false);

        BackupModifierFromSimulations bms = getBackupModifierFromSimulations();
        List<Double> values=bms.backup();

//        List<Double> values=getActionValuesOnPath();
  //      printNodesOnPath();
        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g1,values.get(values.size()-1), DELTA),
                () -> assertEquals(g1*DISCOUNT_FACTOR_SIMULATION_NORMAL,values.get(values.size()-2), DELTA),
                () -> assertEquals(g1*Math.pow(DISCOUNT_FACTOR_SIMULATION_NORMAL,2),values.get(values.size()-3), DELTA)
        );
    }

    @Test public void backupTwoSimulationsResultsOneIsFail() {
        double g1=1, g2=11;  //only g1 is backed up due to fail simulation is rejected
        simulationResults.add(g1,false);
        simulationResults.add(g2,true);

        BackupModifierFromSimulations bms = getBackupModifierFromSimulations();
        List<Double> values=bms.backup();

       // List<Double> values=getActionValuesOnPath();
        //printNodesOnPath();
        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g1,values.get(values.size()-1), DELTA),
                () -> assertEquals(g1*DISCOUNT_FACTOR_SIMULATION_NORMAL,values.get(values.size()-2), DELTA),
                () -> assertEquals(g1*Math.pow(DISCOUNT_FACTOR_SIMULATION_NORMAL,2),values.get(values.size()-3), DELTA)
        );
    }

    @Test public void backupTwoSimulationsResultsBothAreFail() {
        double g1=-10, g2=-10;
        simulationResults.add(g1, true);
        simulationResults.add(g2, true);

        BackupModifierFromSimulations bms = getBackupModifierFromSimulations();
        List<Double> values=bms.backup();

     //   List<Double> values=getActionValuesOnPath();
      //  printNodesOnPath();
        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g1,
                        values.get(values.size()-1), DELTA),
                () -> assertEquals(g1*DISCOUNT_FACTOR_SIMULATION_DEFENSIVE,
                        values.get(values.size()-2), DELTA),
                () -> assertEquals(g1*Math.pow(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE,2),
                        values.get(values.size()-3), DELTA)
        );
    }


    private BackupModifierFromSimulations getBackupModifierFromSimulations() {
        BackupModifierFromSimulations bms=BackupModifierFromSimulations.builder()
                .rootTree(treeRoot)
                .actionsToSelected(actions)
                .actionOnSelected(ACTION_ON_SELECTED)
                .simulationResults(simulationResults)
                .settings(settings).build();

        NodeInterface nodeSelected=treeInfoHelper.getNodeReachedForActions(actions).orElseThrow();
        return bms;
    }

    private void printNodesOnPath() {
        Optional<List<NodeInterface>> nodes = getNodesOnPath();
        nodes.orElseThrow().forEach(System.out::println);
    }

    private List<Double> getActionValuesOnPath() {
        Optional<List<NodeInterface>> nodes = getNodesOnPath();
        List<Action> allActions=Action.getAllActions(actions,ACTION_ON_SELECTED);
        List<Double> values=new ArrayList<>();
        for (NodeInterface node:nodes.orElseThrow()) {
            values.add(node.getActionValue(allActions.get(nodes.orElseThrow().indexOf(node))));
        }
        return values;
    }

    private Optional<List<NodeInterface>> getNodesOnPath() {
        return treeInfoHelper.getNodesOnPathForActions(actions);
    }


    private NodeInterface createMCTSTree(List<Action> actions, State rootState, List<StepReturn> stepReturns) {

        stepReturns.clear();
        State state = rootState.copy();
        NodeInterface nodeRoot = NodeInterface.newNotTerminal(rootState, Action.notApplicable);
        NodeInterface parent = nodeRoot;
        int nofAddedChilds = 0;
        for (Action a : actions) {
            StepReturn sr = stepAndUpdateState(state, a);
             NodeInterface child = NodeInterface.newNotTerminal(sr.newPosition, a);
            if (isNotFinalActionInList(actions, nofAddedChilds)) {
                parent.addChildNode(child);
            }
            parent = child;
            nofAddedChilds++;
        }

        return nodeRoot;
    }

    private boolean isNotFinalActionInList(List<Action> actions, int addedChilds) {
        return addedChilds < actions.size();
    }

    private StepReturn stepAndUpdateState(State pos, Action a) {
        StepReturn sr = environment.step(a, pos);
        pos.setFromReturn(sr);
        return sr;
    }

}
