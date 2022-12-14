package mcts_spacegame;

import mcts_spacegame.enums.Action;
import mcts_spacegame.model_mcts.SimulationReturnsExtractor;
import mcts_spacegame.model_mcts.MonteCarloSettings;
import mcts_spacegame.model_mcts.SimulationResults;
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
    List<Action> actionsOnPath = Arrays.asList(Action.up, Action.up);
    SimulationResults simulationResults;
    MonteCarloSettings settings;

    @Before
    public void init() {
        simulationResults = new SimulationResults();
        settings= MonteCarloSettings.builder()
                .coefficientMaxAverageReturn(1)  //max return
                .discountFactorSimulationNormal(DISCOUNT_FACTOR_SIMULATION_NORMAL)
                .discountFactorSimulationDefensive(DISCOUNT_FACTOR_SIMULATION_DEFENSIVE).build();
    }


    @Test public void backUpOneSimulationResult() {
        double g=1;
        simulationResults.add(g, false);

        SimulationReturnsExtractor bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.simulate();

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

        SimulationReturnsExtractor bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.simulate();

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

        SimulationReturnsExtractor bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.simulate();

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

        SimulationReturnsExtractor bms = getBackupModifierFromSimulations(simulationResults);
        List<Double> values=bms.simulate();

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
    private SimulationReturnsExtractor getBackupModifierFromSimulations(SimulationResults simulationResults) {
        return SimulationReturnsExtractor.builder()
                .nofNodesOnPath(actionsOnPath.size()+1)
                .simulationResults(simulationResults)
                .settings(settings).build();
    }

}
