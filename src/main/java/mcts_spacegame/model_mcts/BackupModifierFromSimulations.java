package mcts_spacegame.model_mcts;

import common.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import mcts_spacegame.enums.Action;
import mcts_spacegame.helpers.TreeInfoHelper;
import mcts_spacegame.models_mcts_nodes.NodeInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/***
 *    Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
 *
 *   from a set of simulations:
 *   1) at least one simulation is terminal-non fail => normal backup from mix of max and average of non-fail simulations
 *   2) all simulations are terminal-fail => defensive backup
 *
 *    normal backup = use discountFactorSimulationNormal
 *    defensive backup = use discountFactorSimulationDefensive
 *
 */

@Log
public class BackupModifierFromSimulations {

    List<NodeInterface> nodesOnPath;
    SimulationResults simulationResults;
    MonteCarloSettings settings;



    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static BackupModifierFromSimulations newBUM(@NonNull NodeInterface rootTree,
                                                  @NonNull List<Action> actionsToSelected,
                                                   @NonNull SimulationResults simulationResults,
                                                  MonteCarloSettings settings) {
        BackupModifierFromSimulations bms=new BackupModifierFromSimulations();
        TreeInfoHelper treeInfoHelper=new TreeInfoHelper(rootTree);
        bms.nodesOnPath = treeInfoHelper.getNodesOnPathForActions(actionsToSelected).orElseThrow();
        bms.simulationResults = simulationResults;

        Conditionals.executeOneOfTwo(Objects.isNull(settings),
                () -> bms.settings = MonteCarloSettings.builder().build(),
                () -> bms.settings = settings);
        return bms;

    }

    public List<Double>  backup() {
        if (simulationResults.size()==0) {
            return new ArrayList<>(Collections.nCopies(nodesOnPath.size(),0d));  //todo into MathUtils
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

}
