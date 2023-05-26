package multi_step_td;

import common.RandUtils;
import multi_step_temp_diff.interfaces.NetworkMemoryInterface;
import multi_step_temp_diff.memory.ForkNeuralValueMemory;
import multi_step_temp_diff.models.ForkEnvironment;
import multi_step_temp_diff.models.NstepExperience;
import multi_step_temp_diff.models.ReplayBufferNStep;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TestForkNeuralValueMemory {
    private static final double DELTA = 2;
    private static final int NOF_ITERATIONS = 1000;
    private static final int BATCH_LENGTH = 30;
    private static final int BUFFER_SIZE = 100;
    NetworkMemoryInterface<Integer> memory;
    Predicate<Integer> isEven=(n) ->  (n % 2 == 0);

    @Before
    public void init() {
        memory=new ForkNeuralValueMemory<>(ForkEnvironment.R_HELL,ForkEnvironment.R_HEAVEN);
    }


    @Test
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect() {
        final double value = 0d;
        ReplayBufferNStep buffer=ReplayBufferNStep.builder()
                .buffer(createBatch(value)).build();
        train(buffer);
        TestHelper.printStateValues(memory);
        TestHelper.assertAllStates(memory,value,DELTA);
    }

    @Test
    public void givenMockedDataAllStatesTen_whenTrain_thenCorrect() {
        final double value = 10d;
        ReplayBufferNStep buffer=ReplayBufferNStep.builder()
                .buffer(createBatch(value)).build();
        train(buffer);
        TestHelper.printStateValues(memory);
        TestHelper.assertAllStates(memory,value,DELTA);
    }

    @Test
    public void givenMockedDataAllStatesMinusTen_whenTrain_thenCorrect() {
        final double value = -10d;
        ReplayBufferNStep buffer=ReplayBufferNStep.builder()
                .buffer(createBatch(value)).build();
        train(buffer);
        TestHelper.printStateValues(memory);
        TestHelper.assertAllStates(memory,value,DELTA);
    }

    @Test
    public void givenMockedDataAllOddStatesMinusTenEvenPlusTen_whenTrain_thenCorrect() {
        final double valueOdd = -10d, valueEven = 10d;
        ReplayBufferNStep buffer=ReplayBufferNStep.builder()
                .buffer(createBatchOddEven(valueOdd,valueEven)).build();
        train(buffer);
        TestHelper.printStateValues(memory);
        assertAllStatesOddEven(valueOdd,valueEven);
    }

    private void train(ReplayBufferNStep buffer) {
        for (int i = 0; i < NOF_ITERATIONS ; i++) {
            memory.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }
    }


    private void assertAllStatesOddEven(double valueOdd, double valueEven) {
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            final double value = isEven.test(si) ? valueEven : valueOdd;
            Assert.assertEquals(value, memory.read(si), DELTA);
        }
    }

    @NotNull
    private List<NstepExperience> createBatch(double value) {
        List<NstepExperience> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            NstepExperience exp= NstepExperience.builder()
                    .stateToUpdate(RandUtils.getRandomIntNumber(0,ForkEnvironment.NOF_STATES))
                    .value(value)
                    .build();
            batch.add(exp);
        }
        return batch;
    }

    @NotNull
    private List<NstepExperience> createBatchOddEven(double valueOdd, double valueEven) {
        List<NstepExperience> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            final int randomIntNumber = RandUtils.getRandomIntNumber(0, ForkEnvironment.NOF_STATES);
            final double value = isEven.test(randomIntNumber) ? valueEven : valueOdd;
            NstepExperience exp= NstepExperience.builder()
                    .stateToUpdate(randomIntNumber)
                    .value(value)
                    .build();
            batch.add(exp);

        }
        return batch;
    }



}
