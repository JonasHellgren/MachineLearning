package mcts_spacegame.model_mcts;

import mcts_spacegame.environment.StepReturn;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.List;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   end node in path is:
 *   1) non fail and terminal => backup all nodes in path  (simulation ends in non-fail state)
 *  *                            backup as 3)              (simulation ends in fail state)
 *   2) non terminal =>  backup all nodes in path  (simulation ends in non-fail state)
 *                       backup as 3)              (simulation ends in fail state)
 *   3) fail and terminal => backup parent of end node AND
 *   every node in path who's children's all are fail and terminal
 *
 *   simulation not applicable for case 3)
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
