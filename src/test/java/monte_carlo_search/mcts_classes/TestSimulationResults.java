package monte_carlo_search.mcts_classes;

import monte_carlo_tree_search.classes.SimulationResults;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSimulationResults {

    private static final double BIG_NEGATIVE_RETURN = -10;
    private static final double DELTA = 0.1;
    private static final boolean IS_ENDING_IN_FAIL = true;
    SimulationResults simulationResults;

    @Before
    public void init() {
        simulationResults = new SimulationResults();
    }

    @Test
    public void whenAddingAllSimulationsTerminalFail_thenCorrect() {
        simulationResults.add(BIG_NEGATIVE_RETURN, true);
        simulationResults.add(BIG_NEGATIVE_RETURN, true);
        simulationResults.add(BIG_NEGATIVE_RETURN, true);

        Assert.assertEquals(3, simulationResults.size());
        Assert.assertTrue(simulationResults.areAllSimulationsTerminalFail());
    }

    @Test public void whenAddingThreeReturtns_thenCorrectMaxReturn() {
        simulationResults.add(0, false);
        simulationResults.add(1, false);
        simulationResults.add(2, false);

        simulationResults.getResults().forEach(System.out::println);

        Assert.assertTrue(simulationResults.maxReturnFromNonFailing().isPresent());
        Assert.assertEquals(2, simulationResults.maxReturnFromNonFailing().orElseThrow(), DELTA);
    }

    @Test public void whenAverageReturn_thenCorrect() {
        simulationResults.add(0, false);
        simulationResults.add(1, false);
        simulationResults.add(2, false);

        Assert.assertTrue(simulationResults.averageReturnFromNonFailing().isPresent());
        Assert.assertEquals(1, simulationResults.averageReturnFromNonFailing().orElseThrow(), DELTA);
    }


    @Test
    public void whenFials_thenAnyFailingReturnIsTrue() {
        simulationResults.add(BIG_NEGATIVE_RETURN+1, IS_ENDING_IN_FAIL);
        simulationResults.add(BIG_NEGATIVE_RETURN, IS_ENDING_IN_FAIL);
        simulationResults.add(BIG_NEGATIVE_RETURN-1, IS_ENDING_IN_FAIL);

        System.out.println("anyFailingReturn = " + simulationResults.anyFailingReturn().orElseThrow());

        Assert.assertTrue(simulationResults.anyFailingReturn().orElseThrow()<BIG_NEGATIVE_RETURN+DELTA+1);
        Assert.assertTrue(simulationResults.anyFailingReturn().orElseThrow()>BIG_NEGATIVE_RETURN-DELTA-1);

    }

    @Test public void whenGetReturnsForFailing_thenCorrect() {
        simulationResults.add(BIG_NEGATIVE_RETURN+1, IS_ENDING_IN_FAIL);
        simulationResults.add(BIG_NEGATIVE_RETURN, IS_ENDING_IN_FAIL);
        simulationResults.add(BIG_NEGATIVE_RETURN-1, IS_ENDING_IN_FAIL);

        System.out.println("returnsForFailing = " + simulationResults.getReturnsForFailing());
        Assert.assertTrue(simulationResults.getReturnsForFailing().contains(BIG_NEGATIVE_RETURN+1d));
        Assert.assertTrue(simulationResults.getReturnsForFailing().contains(BIG_NEGATIVE_RETURN));
        Assert.assertTrue(simulationResults.getReturnsForFailing().contains(BIG_NEGATIVE_RETURN-1d));
    }



}
