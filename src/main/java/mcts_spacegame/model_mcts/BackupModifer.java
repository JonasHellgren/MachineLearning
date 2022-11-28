package mcts_spacegame.model_mcts;

import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
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

    List<Action> actions;
    List<NodeInterface> nodesFromRootToSelected;
    StepReturn stepReturnOfSelected;
    List<List<StepReturn>> simulationResults;

    int nofNodesOnPath;
    int nofActionsOnPath;
    NodeInterface nodeSelected;
    double discountFactor;


    public BackupModifer(List<Action> actions,
                         List<NodeInterface> nodesFromRootToSelected,
                         StepReturn stepReturnOfSelected,
                         List<List<StepReturn>> simulationResults) {
        this.actions=actions;
        this.nodesFromRootToSelected = nodesFromRootToSelected;
        this.stepReturnOfSelected = stepReturnOfSelected;
        this.simulationResults = simulationResults;
        this.nofNodesOnPath= nodesFromRootToSelected.size();
        this.nofActionsOnPath=actions.size();

        if (nofActionsOnPath!= nofNodesOnPath)  {
            System.out.println("actions.size() = " + actions.size());
            System.out.println("nodesFromRootToSelected.size() = " + nodesFromRootToSelected.size());

            throw new IllegalArgumentException("Non compatible sizes of input lists");
        }

        nodeSelected=nodesFromRootToSelected.get(nofNodesOnPath-1);
        discountFactor= DISCOUNT_FACTOR;
    }


    public void backup()  {

     //   double sumRewardsFromTreeSteps=treeSteps.stream().mapToDouble(r -> r.reward).sum();

        NodeInterface nodeSelected=nodesFromRootToSelected.get(nofNodesOnPath-1);

        System.out.println("nodeSelected = " + nodeSelected);


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
        System.out.println("rewards = " + rewards);
        System.out.println("GList = " + GList);
        updateNodesFromReturns(GList,nodesFromRootToSelected);
    }

    @NotNull
    private List<Double> getRewards() {
        List<Double> rewards=new ArrayList<>();
        for (NodeInterface nodeOnPath:nodesFromRootToSelected) {
            if (!nodeOnPath.equals(nodeSelected)) {
                Action action=actions.get(nodesFromRootToSelected.indexOf(nodeOnPath));
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
        NodeInterface nodeSelected=nodesFromRootToSelected.get(nofNodesOnPath-1);

        System.out.println("nofNodesOnPath = " + nofNodesOnPath);
        System.out.println("nodeSelected = " + nodeSelected);

        Action actionInSelected=actions.get(nofActionsOnPath-1);
        updateNode(nodeSelected, stepReturnOfSelected.reward, actionInSelected);
    }

    private void updateNodesFromReturns(List<Double> GList,List<NodeInterface> nodesFromRootToSelected) {
        double G;
        for (NodeInterface node:nodesFromRootToSelected)  {
            Action action=actions.get(nodesFromRootToSelected.indexOf(node));
            G= GList.get(nodesFromRootToSelected.indexOf(node));
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
