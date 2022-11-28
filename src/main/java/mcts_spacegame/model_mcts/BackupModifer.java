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
    List<StepReturn> treeSteps;
    List<List<StepReturn>> simulationResults;
    int nofNodesOnPath;

    double discountFactor;


    public BackupModifer(List<Action> actions,
                         List<NodeInterface> nodesFromRootToSelected,
                         List<StepReturn> treeSteps,
                         List<List<StepReturn>> simulationResults) {
        this.actions=actions;
        this.nodesFromRootToSelected = nodesFromRootToSelected;
        this.treeSteps = treeSteps;
        this.simulationResults = simulationResults;
        this.nofNodesOnPath= nodesFromRootToSelected.size();

        if (actions.size()!= nodesFromRootToSelected.size() || actions.size()!= treeSteps.size())  {
            System.out.println("actions.size() = " + actions.size());
            System.out.println("nodesFromRootToSelected.size() = " + nodesFromRootToSelected.size());
            System.out.println("treeSteps.size() = " + treeSteps.size());

            throw new IllegalArgumentException("Non equal sizes of input lists");
        }

        discountFactor= DISCOUNT_FACTOR;
    }


    public void backup()  {

        double sumRewardsFromTreeSteps=treeSteps.stream().mapToDouble(r -> r.reward).sum();

        NodeInterface nodeSelected=nodesFromRootToSelected.get(nofNodesOnPath-1);

        System.out.println("nodeSelected = " + nodeSelected);

        StepReturn stepReturnOfSelected=treeSteps.get(nofNodesOnPath-1);

        if (!stepReturnOfSelected.isFail)  {
            backupNormalFromTreeSteps();
        } else
        {
            backupDefensiveFromTreeSteps();
        }
    }

    private void backupNormalFromTreeSteps()  {
        List<Double> GList = getgList(treeSteps);
        updateNodesFromReturns(GList,nodesFromRootToSelected);
    }

    private void backupDefensiveFromTreeSteps() {
        log.info("defensiveBackupOfSelectedNode");
        defensiveBackupOfSelectedNode();
        //setParentOfSelectedAsTerminalIfAllItChildrenAreTerminal()  TODO
    }

    private void defensiveBackupOfSelectedNode() {
        NodeInterface nodeSelected=nodesFromRootToSelected.get(nofNodesOnPath-1);
        StepReturn stepReturnOfSelected=treeSteps.get(nofNodesOnPath-1);
        Action actionInSelected=actions.get(nofNodesOnPath-1);
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

    @NotNull
    private List<Double> getgList(List<StepReturn> treeSteps) {
        double G=0;
        List<Double> GList=new ArrayList<>();
        for (int i = treeSteps.size()-1; i >=0 ; i--) {
            StepReturn ei= treeSteps.get(i);
            G=G+ discountFactor *ei.reward;
            GList.add(G);
            }
        Collections.reverse(GList);
        return GList;
    }


}
