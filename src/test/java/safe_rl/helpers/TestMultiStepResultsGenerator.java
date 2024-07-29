package safe_rl.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.domain.agent.value_objects.AgentParameters;
import safe_rl.domain.environment.value_objects.Action;
import safe_rl.domain.trainer.helpers.MultiStepResultsGenerator;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import safe_rl.domain.agent.AgentACDCSafe;
import safe_rl.environments.buying_electricity.SettingsBuying;
import safe_rl.environments.buying_electricity.StateBuying;
import safe_rl.environments.buying_electricity.VariablesBuying;

import java.util.List;

class TestMultiStepResultsGenerator {


    MultiStepResultsGenerator<VariablesBuying> generator;
    AgentACDCSafe<VariablesBuying> agent;
    List<Experience<VariablesBuying>> experiences;

    @BeforeEach
    void init() {
        var parameters= TrainerParameters.newDefault().withGamma(1d).withStepHorizon(3);
        agent= AgentACDCSafe.of(AgentParameters.newDefault(),SettingsBuying.new3HoursSamePrice(),StateBuying.newZero());
        generator=new MultiStepResultsGenerator<>(parameters,agent);
        var exp0=getExpWithReward(0, false);
        var exp1=getExpWithReward(1, false);
        var exp1tTerm=getExpWithReward(1, true);
        experiences=List.of(exp1,exp0,exp1,exp1,exp1tTerm);
    }

    @Test
    void whenReadingCritic_thenZeroValue() {
        double val=agent.readCritic(StateBuying.newZero());
        Assertions.assertEquals(0,val);
    }

    @Test
    void whenGenerating_thenCorrect() {
        var multiStepResults=generator.generate(experiences);
        Assertions.assertEquals(5,multiStepResults.nExperiences());
       // Assertions.assertTrue(multiStepResults.isEqualListLength());
    }

    @Test
    void whenGenerating_thenCorrectResStep0() {
        int step = 0;
        var msr=generator.generate(experiences);
        Assertions.assertEquals(3,msr.valueTarAtStep(step));
        Assertions.assertEquals(3-0,msr.advantageAtStep(step));
        Assertions.assertFalse(msr.isSafeCorrectedAtStep(step));
        Assertions.assertFalse(msr.isFutureOutsideOrTerminalAtStep(step));
    }

    @Test
    void whenGenerating_thenCorrectResStep1() {
        int step = 1;
        var msr=generator.generate(experiences);
        Assertions.assertEquals(3,msr.valueTarAtStep(step));  //up to terminal
        Assertions.assertEquals(3-0,msr.advantageAtStep(step));
        Assertions.assertTrue(msr.isFutureOutsideOrTerminalAtStep(step));
    }

    @Test
    void whenGenerating_thenCorrectResStep3() {
        int step = 3;
        var msr=generator.generate(experiences);
        Assertions.assertEquals(2,msr.valueTarAtStep(step));
        Assertions.assertEquals(2-0,msr.advantageAtStep(step));
        Assertions.assertTrue(msr.isFutureOutsideOrTerminalAtStep(step));
    }


    private static Experience<VariablesBuying> getExpWithReward(int reward, boolean isTerminal) {
        return Experience.notSafeCorrected(
                StateBuying.newZero(), Action.ofDouble(0d), reward, StateBuying.newZero(), isTerminal);
    }



}
