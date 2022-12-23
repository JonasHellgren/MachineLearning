package mcts_classes;

import monte_carlo_tree_search.classes.MonteCarloSettings;
import monte_carlo_tree_search.classes.SimulationResults;
import monte_carlo_tree_search.classes.SimulationReturnsExtractor;
import monte_carlo_tree_search.domains.models_space.ActionShip;
import monte_carlo_tree_search.domains.models_space.ShipActionSet;
import monte_carlo_tree_search.domains.models_space.ShipPolicies;
import monte_carlo_tree_search.domains.models_space.ShipVariables;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
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
    List<ShipActionSet> actionsOnPath = Arrays.asList(ShipActionSet.up, ShipActionSet.up);
    SimulationResults simulationResults;
    MonteCarloSettings<ShipVariables, ShipActionSet> settings;

    @Before
    public void init() {
        simulationResults = new SimulationResults();
        ActionInterface<ShipActionSet> actionTemplate=new ActionShip(ShipActionSet.notApplicable); //whatever action

        settings= MonteCarloSettings.<ShipVariables, ShipActionSet>builder()
                .maxNofTestedActionsForBeingLeafFunction((a) -> actionTemplate.applicableActions().size())
                .firstActionSelectionPolicy(ShipPolicies.newAlwaysStill())
                .simulationPolicy(ShipPolicies.newMostlyStill())
                .coefficientMaxAverageReturn(1)  //max return
                .discountFactorSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE).build();
    }


    @Test public void backUpOneSimulationResult() {
        double g=1;
        simulationResults.add(g, false);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.extract();

        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g,values.get(values.size()-1), DELTA),
                () -> assertEquals(g*DISCOUNT_FACTOR_SIMULATION_NORMAL,values.get(values.size()-2), DELTA),
                () -> assertEquals(g*Math.pow(DISCOUNT_FACTOR_SIMULATION_NORMAL,2),values.get(values.size()-3), DELTA)
        );
    }


    @Test public void backUpTwoSimulationsResults() {
        double g1=1, g2=-1;  //only g1 is backed up due to coefficientMaxAverageReturn(1)
        simulationResults.add(g1, false);
        simulationResults.add(g2, false);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.extract();

        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g1,values.get(values.size()-1), DELTA),
                () -> assertEquals(g1*DISCOUNT_FACTOR_SIMULATION_NORMAL,values.get(values.size()-2), DELTA),
                () -> assertEquals(g1*Math.pow(DISCOUNT_FACTOR_SIMULATION_NORMAL,2),values.get(values.size()-3), DELTA)
        );
    }

    @Test public void backupTwoSimulationsResultsOneIsFail() {
        double g1=1, g2=11;  //only g1 is backed up due to fail simulation is rejected
        simulationResults.add(g1,false);
        simulationResults.add(g2,true);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.extract();

        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g1,values.get(values.size()-1), DELTA),
                () -> assertEquals(g1*DISCOUNT_FACTOR_SIMULATION_NORMAL,values.get(values.size()-2), DELTA),
                () -> assertEquals(g1*Math.pow(DISCOUNT_FACTOR_SIMULATION_NORMAL,2),values.get(values.size()-3), DELTA)
        );
    }

    @Test public void backupTwoSimulationsResultsBothAreFail() {
        double g1=-10, g2=-10;
        simulationResults.add(g1, true);
        simulationResults.add(g2, true);

        SimulationReturnsExtractor<ShipVariables, ShipActionSet> bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.extract();

        System.out.println("values = " + values);

        assertAll(
                () -> assertEquals(g1,
                        values.get(values.size()-1), DELTA),
                () -> assertEquals(g1*DISCOUNT_FACTOR_SIMULATION_DEFENSIVE,
                        values.get(values.size()-2), DELTA),
                () -> assertEquals(g1*Math.pow(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE,2),
                        values.get(values.size()-3), DELTA)
        );
    }
    private SimulationReturnsExtractor<ShipVariables, ShipActionSet> getBackupModifierFromSimulations(SimulationResults simulationResults) {
        return SimulationReturnsExtractor.<ShipVariables, ShipActionSet>builder()
                .nofNodesOnPath(actionsOnPath.size()+1)
                .simulationResults(simulationResults)
                .settings(settings).build();
    }

}
