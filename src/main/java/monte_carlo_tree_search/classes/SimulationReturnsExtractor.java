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
 *    To disable above functionality, "standard" MCTS is applied by settings.isDefensiveBackup=false
 *
 */

@Log
public class SimulationReturnsExtractor<S,A> {

    int nofNodesOnPath;
    SimulationResults simulationResults;
    MonteCarloSettings<S,A> settings;

    //https://stackoverflow.com/questions/30717640/how-to-exclude-property-from-lombok-builder/39920328#39920328
    @Builder
    private static <S,A> SimulationReturnsExtractor<S,A> newBUM(@NonNull Integer nofNodesOnPath,
                                                     @NonNull SimulationResults simulationResults,
                                                     @NonNull MonteCarloSettings<S,A> settings) {
        SimulationReturnsExtractor<S,A> bms=new SimulationReturnsExtractor<>();
        bms.nofNodesOnPath = nofNodesOnPath;
        bms.simulationResults = simulationResults;
        bms.settings = settings;
        return bms;
    }

    public List<Double> extract() {
        if (simulationResults.size() == 0) {
            return new ArrayList<>(Collections.nCopies(nofNodesOnPath, 0d));  //todo into MathUtils
        }

        return (settings.isDefensiveBackup)
                ? createReturnsDefensive()
                : createReturnsAll();
    }

    private List<Double> createReturnsDefensive() {
        return (simulationResults.areAllSimulationsTerminalFail())
            ? createReturnFromSomeFailingUseDefensiveDiscount()
            : createReturnsNonFailing();
    }

    public List<Double> createReturnsAll() {
        log.fine("createReturnsAll");
        double mixReturn = getMixReturnFromAll();
        return getReturns(mixReturn, settings.discountFactorSimulationNormal);
    }

    public List<Double> createReturnsNonFailing() {
        log.fine("createReturnsNonFailing");
        double mixReturn = getMixReturnFromNonFailing();
        return getReturns(mixReturn, settings.discountFactorSimulationNormal);
    }

    public List<Double> createReturnFromSomeFailingUseDefensiveDiscount() {
        log.fine("createReturnFromSomeFailingUseDefensiveDiscount");
        double failReturn=simulationResults.anyFailingReturn().orElseThrow();
        return  getReturns(failReturn, settings.discountFactorSimulationDefensive);
    }

    private double getMixReturnFromNonFailing() {
        double maxReturn=simulationResults.maxReturnFromNonFailing().orElseThrow();
        double avgReturn=simulationResults.averageReturnFromNonFailing().orElseThrow();
        double c=settings.coefficientMaxAverageReturn;
        return c*maxReturn+(1-c)*avgReturn;
    }

    private double getMixReturnFromAll() {
        double maxReturn=simulationResults.maxReturnFromAll().orElseThrow();
        double avgReturn=simulationResults.averageReturnFromAll().orElseThrow();
        double c=settings.coefficientMaxAverageReturn;
        return c*maxReturn+(1-c)*avgReturn;
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
