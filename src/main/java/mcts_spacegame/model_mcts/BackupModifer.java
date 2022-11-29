package mcts_spacegame.model_mcts;

import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   end node in path:
 *   1) terminal-non fail => normal backup
 *   2) non terminal => normal backup
 *   3) terminal-fail => defensive backup (simulations not applicable for case)
 *
 *    normal backup = backup all nodes in path
 *    defensive backup = backup end node AND set it parent as terminal if parents
 *    all children are fail-terminal
 *
 *   a single simulation:
 *   1) terminal-non fail => normal backup
 *   2) terminal-fail =>  defensive backup
 *
 */

@Log
public class BackupModifer {

    private static final int DISCOUNT_FACTOR = 1;

    NodeInterface rootTree;
    List<Action> actionsToSelected;
    Action actionOnSelected;
    StepReturn stepReturnOfSelected;
    List<List<StepReturn>> simulationResults;

    int nofNodesOnPath;
    int nofActionsOnPath;
    NodeInterface nodeSelected;
    List<NodeInterface> nodesOnPath;
    double discountFactor;

    TreeInfoHelper treeInfoHelper;

    public BackupModifer(NodeInterface rootTree,
                         List<Action> actionsToSelected,
                         Action actionOnSelected,
                         StepReturn stepReturnOfSelected,
                         List<List<StepReturn>> simulationResultsOnSelected) {
        this.rootTree = rootTree;
        this.actionsToSelected = actionsToSelected;
        this.actionOnSelected=actionOnSelected;
        this.stepReturnOfSelected = stepReturnOfSelected;
        this.simulationResults = simulationResultsOnSelected;
        this.nofNodesOnPath= actionsToSelected.size();
        this.nofActionsOnPath= actionsToSelected.size();

        treeInfoHelper=new TreeInfoHelper(rootTree);
        nodeSelected=treeInfoHelper.getNodeReachedForActions(actionsToSelected).orElseThrow();  //"No node for action sequence"
        nodesOnPath=treeInfoHelper.getNodesVisitedForActions(actionsToSelected).orElseThrow();

        discountFactor= DISCOUNT_FACTOR;
    }


    public void backup()  {
        if (!stepReturnOfSelected.isFail)  {
            backupNormalFromTreeSteps();
        } else
        {
            backupDefensiveFromTreeSteps();
        }
    }

    private void backupNormalFromTreeSteps()  {
        List<Double> rewards = getRewards();
        List<Double> GList = getReturns(rewards);
        updateNodesFromReturns(GList);
    }

    @NotNull
    private List<Double> getRewards() {
        List<Double> rewards=new ArrayList<>();

        for (NodeInterface nodeOnPath: nodesOnPath) {
            if (!nodeOnPath.equals(nodeSelected)) {
                Action action= actionsToSelected.get(nodesOnPath.indexOf(nodeOnPath));
                rewards.add(nodeOnPath.loadRewardForAction(action));
            }
        }
        rewards.add(stepReturnOfSelected.reward);
        return rewards;
    }

    private void backupDefensiveFromTreeSteps() {
        log.info("defensiveBackupOfSelectedNode");
        defensiveBackupOfSelectedNode();
        //setParentOfSelectedAsTerminalIfAllItChildrenAreTerminal()  TODO
    }

    private void defensiveBackupOfSelectedNode() {

        updateNode(nodeSelected, stepReturnOfSelected.reward, actionOnSelected);
    }

    private void updateNodesFromReturns(List<Double> GList) {
        double G;
        List<Action> actions = TreeInfoHelper.getAllActions(actionsToSelected, actionOnSelected);
        for (NodeInterface node:nodesOnPath)  {
            Action action= actions.get(nodesOnPath.indexOf(node));
            G= GList.get(nodesOnPath.indexOf(node));
            updateNode(node,G, action);
        }
    }

    private void updateNode(NodeInterface node, double G, Action action) {
        node.increaseNofVisits();
        node.increaseNofActionSelections(action);
        node.updateActionValue(G, action);
    }

    @NotNull   //todo streams..
    private List<Double> getReturns(List<Double> rewards) {
        double G=0;
        List<Double> GList=new ArrayList<>();
        for (int i = rewards.size()-1; i >=0 ; i--) {
            double reward= rewards.get(i);
            G=G+ discountFactor *reward;
            GList.add(G);
            }
        Collections.reverse(GList);
        return GList;
    }




}
