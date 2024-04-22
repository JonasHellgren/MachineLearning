package safe_rl.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.List;

class TestMultiStepReturnEvaluator {

    MultiStepReturnEvaluator<VariablesBuying> evaluator;

    @BeforeEach
    void init() {
        var parameter= TrainerParameters.newDefault().withGamma(1d).withStepHorizon(3);
      //  var agent= AgentACDCSafeBuyer.newDefault(BuySettings.new3HoursSamePrice());

        Experience<VariablesBuying> exp0 = Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofDouble(0d), 0, StateBuying.newZero(), false);
        Experience<VariablesBuying> exp1 = Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofDouble(0d), 1, StateBuying.newZero(), false);
        Experience<VariablesBuying> exp0Term = Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofDouble(0d), 0, StateBuying.newZero(), true);
        List<Experience<VariablesBuying>> experiences=
                List.of(exp1,exp0,exp1,exp1,exp0Term);
        evaluator=new MultiStepReturnEvaluator<>(parameter,experiences);
    }

    @Test
    void whenTStartIsZero_whenCorrect() {
        var result=evaluator.evaluate(0);
        Assertions.assertEquals(3,result.sumRewardsNSteps());
        Assertions.assertFalse(result.isFutureStateOutside());
        Assertions.assertFalse(result.isFutureTerminal());
        Assertions.assertNotNull(result.stateFuture());
    }


    @Test
    void whenTStartIsOne_whenCorrect() {
        var result=evaluator.evaluate(1);
        Assertions.assertEquals(2,result.sumRewardsNSteps());
        Assertions.assertFalse(result.isFutureStateOutside());
        Assertions.assertTrue(result.isFutureTerminal());
        Assertions.assertNotNull(result.stateFuture());
    }


    @Test
    void whenTStartIsThree_whenCorrect() {
        var result=evaluator.evaluate(3);
        Assertions.assertEquals(1,result.sumRewardsNSteps());
        Assertions.assertTrue(result.isFutureStateOutside());
        Assertions.assertFalse(result.isFutureTerminal());
        Assertions.assertNull(result.stateFuture());
    }

}
