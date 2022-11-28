package mcts_spacegame.model_mcts;

import black_jack.models_episode.EpisodeItem;
import mcts_spacegame.enums.Action;
import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   end node in path:
 *   1) terminal-non fail => normal backup
 *   2) non terminal => normal backup
 *   3) terminal-non fail => defensive backup (simulations not applicable for case)
 *
 *    normal backup = backup all nodes in path
 *    defensive backup = backup parent of end node AND set grand parent as terminal if its
 *    all children's all are fail-terminal
 *
 *   a single simulation:
 *   1) terminal-non fail => normal backup
 *   2) terminal-fail =>  defensive backup
 *
 */

public class BackupModifer {

    private static final int DISCOUNT_FACTOR = 1;

    List<Action> actions;
    List<NodeInterface> nodesFromRootToSelected;
    List<StepReturn> treeSteps;
    List<List<StepReturn>> simulationResults;

    double discountFactor;


    public BackupModifer(List<Action> actions,
                         List<NodeInterface> nodesFromRootToSelected,
                         List<StepReturn> treeSteps,
                         List<List<StepReturn>> simulationResults) {
        this.actions=actions;
        this.nodesFromRootToSelected = nodesFromRootToSelected;
        this.treeSteps = treeSteps;
        this.simulationResults = simulationResults;

        if (actions.size()!= nodesFromRootToSelected.size() || actions.size()!= treeSteps.size())  {
            throw new IllegalArgumentException("Non equal sizes of input lists");
        }

        discountFactor= DISCOUNT_FACTOR;
    }


    public void backup()  {

        double sumRewardsFromTreeSteps=treeSteps.stream().mapToDouble(r -> r.reward).sum();

        int lengthPath= nodesFromRootToSelected.size();
        NodeInterface endNode=nodesFromRootToSelected.get(lengthPath-1);

        if (endNode.isTerminalNoFail() || endNode.isNotTerminal())  {
            backupNormalFromTreeSteps();
        } else
        {
           // backupDefensiveFromTreeSteps();
        }
    }

    private void backupNormalFromTreeSteps()  {
        double G=0;
        List<Double> GList=new ArrayList<>();
        for (int i = treeSteps.size()-1; i >=0 ; i--) {
            StepReturn ei= treeSteps.get(i);
            G=G+ discountFactor *ei.reward;
            GList.add(G);
            }
        Collections.reverse(GList);
        System.out.println("GList = " + GList);


        for (NodeInterface node:nodesFromRootToSelected)  {
            Action action=actions.get(nodesFromRootToSelected.indexOf(node));
            G= GList.get(nodesFromRootToSelected.indexOf(node));
            node.increaseNofVisits();
            node.increaseNofActionSelections(action);
            node.updateActionValue(G,action);
        }


    }


}
