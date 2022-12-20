package monte_carlo_tree_search.classes;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/***
 *   This class analyses a set of simulation results, i.e. simulationResults. The result is a list of returns.
 *   These returns are returned from backup() and later used by another class BackupModifier to modify nodes on the tree
 *   selection path.
 *
 *   Fail states normally gives big negative rewards, to avoid destructive backup, measures below are taken
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
public class SimulationReturnsExtractor<SSV,AV> {

    int nofNodesOnPath;
    SimulationResults simulationResults;
    MonteCarloSettings<SSV,AV> settings;

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static <SSV,AV> SimulationReturnsExtractor<SSV,AV> newBUM(@NonNull Integer nofNodesOnPath,
                                                     @NonNull SimulationResults simulationResults,
                                                     @NonNull MonteCarloSettings<SSV,AV> settings) {
        SimulationReturnsExtractor<SSV,AV> bms=new SimulationReturnsExtractor<>();
        bms.nofNodesOnPath = nofNodesOnPath;
        bms.simulationResults = simulationResults;
        bms.settings = settings;
        return bms;
    }

    public List<Double> simulate() {
        if (simulationResults.size() == 0) {
            return new ArrayList<>(Collections.nCopies(nofNodesOnPath, 0d));  //todo into MathUtils
        }

        List<Double> returnsSimulation;
        if (!simulationResults.areAllSimulationsTerminalFail()) {
            returnsSimulation = createReturnsNormal();
        } else {
            returnsSimulation = createReturnsDefensive();
        }
        return returnsSimulation;
    }

    public List<Double> createReturnsNormal() {
        log.fine("backupNormal");
        double maxReturn=simulationResults.maxReturn().orElseThrow();
        double avgReturn=simulationResults.averageReturn().orElseThrow();
        double c=settings.coefficientMaxAverageReturn;
        double mixReturn=c*maxReturn+(1-c)*avgReturn;
        return getReturns(mixReturn, settings.discountFactorSimulationNormal);
    }


    public List<Double> createReturnsDefensive() {
        log.fine("backupDefensive");
        double failReturn=simulationResults.anyFailingReturn().orElseThrow();
        return  getReturns(failReturn, settings.discountFactorSimulationDefensive);
    }

    /**
     *    nodesOnPath = (r)  ->  (1) ->  (2) ->  (3) ->  (4)
     *    node i will have discount of discountFactor^nofNodesRemaining
     *    so for this example with discountFactor=0.9 => discount (r) is 0.9^(5-0-1)=0.9^4 and for (4) it is 0.9^(5-4-1) = 1
     */

    private List<Double> getReturns(double singleReturn, double discountFactor) {
        List<Double> returnsSimulation=new ArrayList<>();
        for (int ni = 0; ni < nofNodesOnPath ; ni++) {
            int nofNodesRemaining=nofNodesOnPath-ni-1;
            double discount=Math.pow(discountFactor,nofNodesRemaining);
            returnsSimulation.add(singleReturn*discount);
        }
        return returnsSimulation;
    }

}
