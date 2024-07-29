package safe_rl.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.trainer.helpers.MultiStepReturnEvaluator;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestMultiStepReturnEvaluator {

    MultiStepReturnEvaluator<VariablesBuying> evaluator;

    @BeforeEach
    void init() {
        var parameter= TrainerParameters.newDefault().withGamma(1d).withStepHorizon(3);
        var exp0 = getExpWithReward(0);
        var exp1 = getExpWithReward(1);
        Experience<VariablesBuying> exp0Term = getNotCorrectedExpWithReward(0);
        var experiences=List.of(exp1,exp0,exp1,exp1,exp0Term);
        evaluator=new MultiStepReturnEvaluator<>(parameter,experiences);
    }

    @Test
    void whenTStartIsZero_whenCorrect() {
        var result=evaluator.evaluate(0);
        assertEquals(3,result.sumRewardsNSteps());
        assertFalse(result.isFutureStateOutside());
        assertFalse(result.isFutureTerminal());
        assertNotNull(result.stateFuture());
    }


    @Test
    void whenTStartIsOne_whenCorrect() {
        var result=evaluator.evaluate(1);
        assertEquals(2,result.sumRewardsNSteps());
        assertFalse(result.isFutureStateOutside());
        assertTrue(result.isFutureTerminal());
        assertNotNull(result.stateFuture());
    }


    @Test
    void whenTStartIsThree_whenCorrect() {
        var result=evaluator.evaluate(3);
        assertEquals(1,result.sumRewardsNSteps());
        assertTrue(result.isFutureStateOutside());
        assertFalse(result.isFutureTerminal());
        assertNull(result.stateFuture());
    }

    private static Experience<VariablesBuying> getNotCorrectedExpWithReward(int reward) {
        return Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofDouble(0d), reward, StateBuying.newZero(), true);
    }

    private static Experience<VariablesBuying> getExpWithReward(int reward) {
        return Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofDouble(0d), reward, StateBuying.newZero(), false);
    }


}
