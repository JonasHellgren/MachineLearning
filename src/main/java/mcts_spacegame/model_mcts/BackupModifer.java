package mcts_spacegame.model_mcts;

import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.List;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   end node in path:
 *   1) non fail and terminal => normal backup
 *   2) non terminal => normal backup
 *   3) fail and terminal => defensive backup (simulations not applicable for case)
 *
 *    normal backup = backup all nodes in path
 *    defensive backup = backup parent of end node AND
 *    every node in path who's children's all are fail and terminal
 *
 *   a single simulation:
 *   1) non fail and terminal => normal backup
 *   2) fail and terminal =>  defensive backup
 *
 */

public class BackupModifer {

    List<NodeInterface> nodesFromRootToSelected;
    List<StepReturn> treeSteps;
    List<List<StepReturn>> simulationResults;

    public BackupModifer(List<NodeInterface> nodesFromRootToSelected,
                         List<StepReturn> treeSteps,
                         List<List<StepReturn>> simulationResults) {
        this.nodesFromRootToSelected = nodesFromRootToSelected;
        this.treeSteps = treeSteps;
        this.simulationResults = simulationResults;
    }


    public void backup()  {

        double sumRewardsFromTreeSteps=treeSteps.stream().mapToDouble(r -> r.reward).sum();

        int lengthPath= nodesFromRootToSelected.size();
        NodeInterface endNode=nodesFromRootToSelected.get(lengthPath-1);

        if (endNode.isTerminalNoFail() || endNode.isNotTerminal())  {
            //backupNormalFromTreeSteps();
        } else
        {
           // backupDefensiveFromTreeSteps();
        }


    }


}
