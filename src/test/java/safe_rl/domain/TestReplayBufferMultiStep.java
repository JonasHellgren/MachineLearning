package safe_rl.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import safe_rl.domain.memories.ReplayBufferMultiStepExp;
import safe_rl.domain.value_classes.ExperienceMultiStep;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;

public class TestReplayBufferMultiStep {

    public static final int MAX_SIZE = 100;
    ReplayBufferMultiStepExp<VariablesTrading> buffer;
    ExperienceMultiStep<VariablesTrading> experience;

    @Before
    public void init() {
        buffer= ReplayBufferMultiStepExp.newFromMaxSize(MAX_SIZE);
        experience= ExperienceMultiStep.of(
                StateTrading.newFullAndFresh(),0d,StateTrading.newFullAndFresh(),false);

                /*new ExperienceMultiStep<>(StateTrading.newFullAndFresh(),
                List.of(Action.ofDouble(1d)),List.of(0d)
                ,StateTrading.newFullAndFresh(), false);*/
    }

    @Test
    public void whenAddingOneExperience_thenExists() {
        buffer.add(experience);
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
        buffer.add(experience);
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
            buffer.add(experience);;
        }
    }

}
