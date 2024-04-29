package safe_rl.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import safe_rl.domain.abstract_classes.Action;
import safe_rl.domain.memories.ReplayBuffer;
import safe_rl.domain.memories.ReplayBufferMultiStepExp;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

import java.util.List;

public class TestReplayBufferMultiStep {

    public static final int MAX_SIZE = 100;
    ReplayBufferMultiStepExp<VariablesTrading> buffer;
    ExperienceMultiStep<VariablesTrading> experience;

    @Before
    public void init() {
        buffer= ReplayBufferMultiStepExp.newFromMaxSize(MAX_SIZE);
        experience=new ExperienceMultiStep<>(StateTrading.newFullAndFresh(),
                List.of(Action.ofDouble(1d)),List.of(0d)
                ,StateTrading.newFullAndFresh(), false);
    }

    @Test
    public void whenAddingOneExperience_thenExists() {
        buffer.addExperience(experience);
        System.out.println("buffer = " + buffer);
        Assert.assertEquals(1,buffer.size());
    }

    @Test
    public void whenAddingTenExperiences_thenExists() {
        addMany(10);
        Assert.assertEquals(10,buffer.size());
    }

    @Test
    public void whenAddingAndFull_thenCorrect() {
        addMany(MAX_SIZE);
        Assertions.assertTrue(buffer.isFull());
        buffer.addExperience(experience);
        Assert.assertEquals(MAX_SIZE,buffer.size());
    }


    @Test
    public void whenGetMiniBatch_thenCorrect() {
        addMany(10);
        int batchLength = 5;
        var miniBuffer=buffer.getMiniBatch(batchLength);
        System.out.println("buffer = " + buffer);
        miniBuffer.forEach(System.out::println);
        Assert.assertEquals(batchLength,miniBuffer.size());
    }


    private void addMany(int nofExp) {
        for (int i = 0; i < nofExp; i++) {
            buffer.addExperience(experience);;
        }
    }

}
