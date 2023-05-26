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
    NetworkMemoryInterface<Integer> memory;

    Predicate<Integer> isEven=(n) ->  n>5; // (n % 2 == 0);

    @Before
    public void init() {
        memory=new ForkNeuralValueMemory<>(ForkEnvironment.R_HELL,ForkEnvironment.R_HEAVEN);
    }


    @Test
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect() {
        final double value = 0d;
        List<NstepExperience> batch = createBatch(value);
        memory.learn(batch);
        printStateValues();
        assertAllStates(value);
    }

    @Test
    public void givenMockedDataAllStatesTen_whenTrain_thenCorrect() {
        final double value = 10d;
        List<NstepExperience> batch = createBatch(value);
        memory.learn(batch);
        printStateValues();
        assertAllStates(value);
    }

    @Test
    public void givenMockedDataAllStatesMinusTen_whenTrain_thenCorrect() {
        final double value = -10d;
        List<NstepExperience> batch = createBatch(value);
        memory.learn(batch);
        printStateValues();
        assertAllStates(value);
    }

    @Test
    public void givenMockedDataAllOddStatesMinusTenEvenPlusTen_whenTrain_thenCorrect() {
        final double valueOdd = -10d, valueEven = 10d;
        List<NstepExperience> batch = createBatchOddEven(valueOdd,valueEven);
        ReplayBufferNStep buffer=ReplayBufferNStep.builder().buffer(batch).build();

        for (int i = 0; i < NOF_ITERATIONS ; i++) {
            memory.learn(buffer.getMiniBatch(30));
        }
        printStateValues();
        //assertAllStates(value);
    }


    private void assertAllStates(double value) {
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            Assert.assertEquals(value, memory.read(si), DELTA);
        }
    }

    @NotNull
    private List<NstepExperience> createBatch(double value) {
        List<NstepExperience> batch=new ArrayList<>();
        for (int i = 0; i < NOF_ITERATIONS; i++) {
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
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            final int randomIntNumber = RandUtils.getRandomIntNumber(0, ForkEnvironment.NOF_STATES);
            final double value = isEven.test(randomIntNumber) ? valueEven : valueOdd;
            NstepExperience exp= NstepExperience.builder()
                    .stateToUpdate(randomIntNumber)
                    .value(value)
                    .build();
            batch.add(exp);


        //    System.out.println("randomIntNumber = " + randomIntNumber);
        //    System.out.println("value = " + value);

        }
        return batch;
    }

    private void printStateValues() {
        Map<Integer,Double> stateValues=new HashMap<>();
        for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
            stateValues.put(si,memory.read(si));
        }
        System.out.println("stateValues = " + stateValues);
    }

}
