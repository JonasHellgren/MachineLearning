package safe_rl.domain.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.trainer.helpers.MultiStepResultsGenerator;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestEvaluateRewardsSumInMultiStepResultsGenerator {

    public static final int REWARD = 0;
    MultiStepResultsGenerator<VariablesBuying> generator;
    List<Experience<VariablesBuying>> experiences;

    @BeforeEach
    void init() {
        var parameter= TrainerParameters.newDefault().withGamma(1d).withStepHorizon(3);
        var exp0 = getExpWithReward(REWARD);
        var exp1 = getExpWithReward(1);
        Experience<VariablesBuying> exp0Term = getNotCorrectedExpWithReward(REWARD);
        experiences=List.of(exp1,exp0,exp1,exp1,exp0Term);
        var notUsedAgent= AgentACDCSafe.of(
                AgentParameters.newDefault(),
                SettingsBuying.new3HoursSamePrice(),
                StateBuying.newZero());
        generator=new MultiStepResultsGenerator<>(parameter,notUsedAgent);
    }

    @Test
    void whenTStartIsZero_whenCorrect() {
        var result=generator.evaluateRewardsSum(REWARD,experiences);
        assertEquals(3,result.sumRewardsNSteps());
        assertFalse(result.isFutureStateOutside());
        assertFalse(result.isFutureTerminal());
        assertNotNull(result.stateFuture());
    }


    @Test
    void whenTStartIsOne_whenCorrect() {
        var result=generator.evaluateRewardsSum(1,experiences);
        assertEquals(2,result.sumRewardsNSteps());
        assertFalse(result.isFutureStateOutside());
        assertTrue(result.isFutureTerminal());
        assertNotNull(result.stateFuture());
    }


    @Test
    void whenTStartIsThree_whenCorrect() {
        var result=generator.evaluateRewardsSum(3,experiences);
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
