package mcts_spacegame.model_mcts;

import common.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken

 *    normal backup = use alphaNormal and discountFactorSimulationNormal
 *    defensive backup = use alphaDefensive and discountFactorSimulationDefensive
 *
 *   a single simulation:
 *   1) at least one simulation is terminal-non fail => normal backup from mix of max and average of non-fail simulations
 *   2) all simulations are terminal-fail => defensive backup
 *
 *
 */

@Log
public class BackupModifierFromSimulations extends BackupModifierAbstract {

    SimulationResults simulationResults;

    public BackupModifierFromSimulations(NodeInterface rootTree,
                                   List<Action> actionsToSelected,
                                   Action actionOnSelected,
                                   SimulationResults simulationResults,
                                   MonteCarloSettings settings) {
        super(rootTree, actionsToSelected, actionOnSelected, settings);
        this.simulationResults=simulationResults;
    }

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifierFromSimulations newBUM(NodeInterface rootTree,
                                                  @NonNull List<Action> actionsToSelected,
                                                  @NonNull Action actionOnSelected,
                                                  @NonNull SimulationResults simulationResults,
                                                  MonteCarloSettings settings) {
        return new BackupModifierFromSimulations(rootTree,
                actionsToSelected,
                actionOnSelected,
                simulationResults,
                settings);
    }

    public List<Double>  backup() {
        if (simulationResults.size()==0) {
            return new ArrayList<>(Collections.nCopies(nodesOnPath.size(),0d));
        }
        List<Double> returnsSimulation;
        if(!simulationResults.areAllSimulationsTerminalFail()) {
            returnsSimulation=backupNormal(); } else {
                returnsSimulation=backupDefensive(); }
        return returnsSimulation;
    }

    public List<Double> backupNormal() {
        log.fine("backupNormal");
        double maxReturn=simulationResults.maxReturn().orElseThrow();
        double avgReturn=simulationResults.averageReturn().orElseThrow();
        double c=settings.coefficientMaxAverageReturn;
        double mixReturn=c*maxReturn+(1-c)*avgReturn;
        return updateNodesFromReturn(mixReturn, settings.discountFactorSimulationNormal);
    }


    public List<Double>  backupDefensive() {
        log.fine("backupDefensive");
        double failReturn=simulationResults.anyFailingReturn().orElseThrow();
        return  updateNodesFromReturn(failReturn, settings.discountFactorSimulationDefensive);
    }


    /**
     *    nodesOnPath = (r)  ->  (1) ->  (2) ->  (3) ->  (4)
     *    node i will have discount of discountFactor^nofNodesRemaining
     *    so for this example with discountFactor=0.9 => discount (r) is 0.9^(5-0-1)=0.9^4 and for (4) it is 0.9^(5-4-1) = 1
     */

    private List<Double> updateNodesFromReturn(double singleReturn, double discountFactor) {
       // List<Action> actions = Action.getAllActions(actionsToSelected, actionOnSelected);
        int nofNodesOnPath = nodesOnPath.size();
        List<Double> returnsSimulation=new ArrayList<>();
        for (NodeInterface node : nodesOnPath) {
            //Action action = actions.get(nodesOnPath.indexOf(node));
            int nofNodesRemaining=nofNodesOnPath-nodesOnPath.indexOf(node)-1;
            double discount=Math.pow(discountFactor,nofNodesRemaining);
            returnsSimulation.add(singleReturn*discount);
        }
        return returnsSimulation;
    }
/*
    private void updateNode(NodeInterface node, double singleReturn, Action action, double alpha) {
        node.updateActionValue(singleReturn, action, alpha);
    }
*/

}
