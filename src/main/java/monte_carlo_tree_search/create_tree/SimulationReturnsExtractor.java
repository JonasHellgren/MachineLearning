package monte_carlo_tree_search.create_tree;
import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import monte_carlo_tree_search.models_and_support_classes.SimulationResults;

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
 *    One can debate "but shall not discountFactorSimulation be reduced instead?". The problem is that when doing
 *    simulations, it is not known if they all will give fail states (require defensive backup). So even if not
 *    perfectly clean, the concept discountedReturns is used.
 *
 *    To disable above functionality, "standard" MCTS is applied by settings.isDefensiveBackup=false
 *
 */

@Log
public class SimulationReturnsExtractor<S,A> {

    private static final double SIMULATION_RETURN_BACKUP = 0d;
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

    public List<Double> getSimulationReturns() {
        if (simulationResults.size() == 0) {
            log.fine("No simulation results present");
            return new ArrayList<>(Collections.nCopies(nofNodesOnPath, SIMULATION_RETURN_BACKUP));
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
        return getDiscountedReturns(mixReturn, settings.discountFactorBackupSimulationNormal);
    }

    public List<Double> createReturnsNonFailing() {
        log.fine("createReturnsNonFailing");
        double mixReturn = getMixReturnFromNonFailing();
        return getDiscountedReturns(mixReturn, settings.discountFactorBackupSimulationNormal);
    }

    public List<Double> createReturnFromSomeFailingUseDefensiveDiscount() {
        log.fine("createReturnFromSomeFailingUseDefensiveDiscount");
        double failReturn=simulationResults.anyFailingReturn().orElseThrow();
        return  getDiscountedReturns(failReturn, settings.discountFactorBackupSimulationDefensive);
    }

    private double getMixReturnFromNonFailing() {
        double maxReturn=simulationResults.maxReturnFromNonFailing().orElseThrow();
        double avgReturn=simulationResults.averageReturnFromNonFailing().orElseThrow();
        return calculateMixReturn(maxReturn, avgReturn);
    }

    private double getMixReturnFromAll() {
        double maxReturn=simulationResults.maxReturnFromAll().orElseThrow();
        double avgReturn=simulationResults.averageReturnFromAll().orElseThrow();
        return calculateMixReturn(maxReturn, avgReturn);
    }

    private double calculateMixReturn(double maxReturn, double avgReturn) {
        double c = settings.coefficientMaxAverageReturn;
        return settings.coefficientMaxAverageReturn * maxReturn + (1 - c) * avgReturn;
    }

    /**
     *  nodesOnPath = (r)  -> (0) -> (1) ->  (2)
     *  discountFactor=1, singleReturn=1 => returns=[1,1,1] => discountedReturns=[1,1,1]
     *  discountFactor=0.5, singleReturn=1 => returns=[1,1,1] => discountedReturns=[0.5^2*1,0.5^1*1,0.5^0*1]=[0.25,0.5,1]
     */

    private List<Double> getDiscountedReturns(double singleReturn, double discountFactor) {
        List<Double> returns = ListUtils.createListWithEqualElementValues(nofNodesOnPath,singleReturn);
        return ListUtils.discountedElementsReverse(returns,discountFactor);
    }


}
