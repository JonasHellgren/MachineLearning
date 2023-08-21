package multi_step_td.fork;

import common.RandUtils;
import multi_step_td.helpers.StateAsserter;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.helpers_specific.ForkAndMazeHelper;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizeMinMax;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TestForkNeuralValueMemory {
    private static final double DELTA = 2;
    private static final int NOF_ITERATIONS = 2000;
    private static final int BATCH_LENGTH = 30;
    private static final int BUFFER_SIZE = 100;
    public static final double LEARNING_RATE = 1e-1;
    public static final int DISCOUNT_FACTOR = 1;

    Predicate<Integer> isEven=(n) ->  (n % 2 == 0);


    NetworkMemoryInterface<ForkVariables> memory;
    EnvironmentInterface<ForkVariables> environment;
    ForkAndMazeHelper<ForkVariables> helper;
    StateAsserter<ForkVariables> stateAsserter;
    ForkEnvironmentSettings envSettings;


    @BeforeEach
    public void init() {
        environment = new ForkEnvironment();

        ForkEnvironment envCasted=(ForkEnvironment) environment;
        envSettings=envCasted.envSettings;
        double minOut = envSettings.rewardHell();
        double maxOut = envSettings.rewardHeaven();
        NetSettings netSettings = NetSettings.builder()
                .learningRate(LEARNING_RATE)
                .inputSize(envSettings.nofStates()).nofNeuronsHidden(envSettings.nofStates())
                .minOut(minOut).maxOut(maxOut)
                .transferFunctionType(TransferFunctionType.TANH)
                .normalizer(new NormalizeMinMax(minOut,maxOut)).build();

        AgentForkNeural agent=AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment,
                DISCOUNT_FACTOR,
                netSettings);
        memory=agent.getMemory();
        helper=new ForkAndMazeHelper<>(agent, environment);
        stateAsserter=new StateAsserter<>(agent,environment);
    }


    @Test
    @Tag("nettrain")
    public void givenMockedDataAllStatesZero_whenTrain_thenCorrect() {
        final double value = 0d;
        ReplayBufferNStepUniform<ForkVariables> buffer= ReplayBufferNStepUniform.<ForkVariables>builder()
                .buffer(createBatch(value)).build();
        train(buffer);
        helper.printStateValues();
        stateAsserter.assertAllStatesExceptTerminal(value,DELTA);
    }

    @Test
    @Tag("nettrain")
    public void givenMockedDataAllStatesTen_whenTrain_thenCorrect() {
        final double value = 10d;
        ReplayBufferNStepUniform<ForkVariables> buffer= ReplayBufferNStepUniform.<ForkVariables>builder()
                .buffer(createBatch(value)).build();
        train(buffer);
        helper.printStateValues();
        stateAsserter.assertAllStatesExceptTerminal(value,DELTA);
    }

    @Test
    @Tag("nettrain")
    public void givenMockedDataAllStatesMinusTen_whenTrain_thenCorrect() {
        final double value = -10d;
        ReplayBufferNStepUniform<ForkVariables> buffer= ReplayBufferNStepUniform.<ForkVariables>builder()
                .buffer(createBatch(value)).build();
        train(buffer);
        helper.printStateValues();
        stateAsserter.assertAllStatesExceptTerminal(value,DELTA);
    }

    @Test
    @Tag("nettrain")
    public void givenMockedDataAllOddStatesMinusTenEvenPlusTen_whenTrain_thenCorrect() {
        final double valueOdd = -10d, valueEven = 10d;
        ReplayBufferNStepUniform<ForkVariables> buffer= ReplayBufferNStepUniform.<ForkVariables>builder()
                .buffer(createBatchOddEven(valueOdd,valueEven)).build();
        train(buffer);

        helper.printStateValues();
        assertAllStatesOddEven(valueOdd,valueEven);
    }

    @Test
    @Tag("nettrain")
    public void givenMockedDataAllStatesZero_whenTrainWithWeights_thenCorrect() {
        final double value = 0d;
        ReplayBufferNStepUniform<ForkVariables> buffer= ReplayBufferNStepUniform.<ForkVariables>builder()
                .buffer(createBatch(value)).build();
        trainWithWeights(buffer);
        helper.printStateValues();
        stateAsserter.assertAllStatesExceptTerminal(value,DELTA);
    }

    private void train(ReplayBufferNStepUniform<ForkVariables> buffer) {
        for (int i = 0; i < NOF_ITERATIONS ; i++) {
            memory.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }
    }

    private void trainWithWeights(ReplayBufferNStepUniform<ForkVariables> buffer) {
        for (int i = 0; i < NOF_ITERATIONS ; i++) {
            memory.learnUsingWeights(buffer.getMiniBatch(BATCH_LENGTH));
        }
    }


    private void assertAllStatesOddEven(double valueOdd, double valueEven) {
        //for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
        for (StateInterface<ForkVariables> si:environment.stateSet()) {
            final double value = isEven.test(si.getVariables().position) ? valueEven : valueOdd;
            Assert.assertEquals(value, memory.read(si), DELTA);
        }
    }

    @NotNull
    private List<NstepExperience<ForkVariables>> createBatch(double value) {
        List<NstepExperience<ForkVariables>> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            NstepExperience<ForkVariables> exp= NstepExperience.<ForkVariables>builder()
                    .stateToUpdate(ForkState.newFromRandomPos())
                    .weight(1d)
                    .value(value)
                    .build();
            batch.add(exp);
        }
        return batch;
    }

    @NotNull
    private List<NstepExperience<ForkVariables>> createBatchOddEven(double valueOdd, double valueEven) {
        List<NstepExperience<ForkVariables>> batch=new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            final int randomIntNumber = RandUtils.getRandomIntNumber(0, envSettings.nofStates());
            final double value = isEven.test(randomIntNumber) ? valueEven : valueOdd;
            NstepExperience<ForkVariables> exp= NstepExperience.<ForkVariables>builder()
                    .stateToUpdate(ForkState.newFromPos(randomIntNumber))
                    .value(value)
                    .build();
            batch.add(exp);

        }
        return batch;
    }



}
