package monte_carlo_search.mcts_classes;

import monte_carlo_tree_search.create_tree.MonteCarloSettings;
import monte_carlo_tree_search.models_and_support_classes.SimulationResults;
import monte_carlo_tree_search.create_tree.SimulationReturnsExtractor;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipPolicies;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSimulationReturnsExtractor {

    private static final double DELTA = 0.1;
    private static final double DISCOUNT_FACTOR_SIMULATION_NORMAL = 0.9;
    private static final double DISCOUNT_FACTOR_SIMULATION_DEFENSIVE = 0.1;
    private static final int INDEX_0 = 0;
    private static final int INDEX_1 = 1;
    private static final int INDEX_2 = 2;

    List<ShipActionSet> actionsOnPath = Arrays.asList(ShipActionSet.up, ShipActionSet.up);
    SimulationResults simulationResults;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;

    @Before
    public void init() {
        simulationResults = new SimulationResults();
        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .actionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1)  //max return
                .discountFactorBackupSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorBackupSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE).build();
    }


    @Test public void whenBackUpOneSimulationResult_thenCorrect() {
        double g=1;
        simulationResults.add(g, false);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.getSimulationReturns();

        System.out.println("values = " + values);

        assertValues(g,DISCOUNT_FACTOR_SIMULATION_NORMAL, values);
    }


    @Test public void whenBackUpTwoSimulationsResults_thenCorrect() {
        double g1=1, g2=-1;  //only g1 is backed up due to coefficientMaxAverageReturn(1)
        simulationResults.add(g1, false);
        simulationResults.add(g2, false);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.getSimulationReturns();

        System.out.println("values = " + values);

        assertValues(g1,DISCOUNT_FACTOR_SIMULATION_NORMAL, values);
    }



    @Test public void whenBackupTwoSimulationsResults_thenOneIsFail() {
        double g1=1, g2=11;  //only g1 is backed up due to fail simulation is rejected
        simulationResults.add(g1,false);
        simulationResults.add(g2,true);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.getSimulationReturns();

        System.out.println("values = " + values);

        assertValues(g1,DISCOUNT_FACTOR_SIMULATION_NORMAL, values);
    }

    @Test public void whenBackupTwoSimulationsResults_thenBothAreFail() {
        double g1=-10, g2=-10;
        simulationResults.add(g1, true);
        simulationResults.add(g2, true);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.getSimulationReturns();

        System.out.println("values = " + values);

        assertValues(g1,DISCOUNT_FACTOR_SIMULATION_DEFENSIVE, values);

    }
    private void assertValues(double g1, double DF, List<Double> values) {
        assertAll(
                () -> assertEquals(g1*Math.pow(DF,2),values.get(INDEX_0), DELTA),
                () -> assertEquals(g1*DF,values.get(INDEX_1), DELTA),
                () -> assertEquals(g1,values.get(INDEX_2), DELTA)
        );
    }

    private SimulationReturnsExtractor<ShipVariables, ShipActionSet> getBackupModifierFromSimulations(SimulationResults simulationResults) {
        return SimulationReturnsExtractor.<ShipVariables, ShipActionSet>builder()
                .nofNodesOnPath(actionsOnPath.size()+1)
                .simulationResults(simulationResults)
                .settings(settings).build();
    }

}
