package multi_step_td.charge;

import multi_step_temp_diff.domain.test_helpers.AgentNeuralChargeTestHelper;
import multi_step_temp_diff.domain.helpers.MockedReplayBufferCreatorCharge;
import multi_step_temp_diff.domain.test_helpers.ChargeStateSuppliers;
import multi_step_temp_diff.domain.test_helpers.StateToValueFunctionContainerCharge;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.normalizer.NormalizerMeanStd;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.neuroph.util.TransferFunctionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/***
 * MeanStd normalizer gives massive improvement versus MinMax
 * Many hidden layers (deep) and few neurons seems to be better than the opposite
 * TANH or GAUSSIAN is not critical
 * momentum is not critical but non-zero seems better
 * NOF_ITERATIONS_RULE should be approx 10 k for convergence, takes long time
 *
 */

public class TestAgentNeuralChargeMockedData {

    private static final int BUFFER_SIZE = 10_000, NOF_ITERATIONS_FIXED =500, NOF_ITERATIONS_RULE = 2_000;
    private static final int BATCH_LENGTH = 100;
    public static final double DELTA = 5;
    public static final int ITERATIONS_BETWEEN_PRINTI = 100;
    public static final double VALUE_IF_NOT_OCCUPIED = 1.1d;
    public static final NormalizerMeanStd NORMALIZER_ONEDOTONE =
            new NormalizerMeanStd(List.of(0.3, 0.5, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d));
    public static final double SOC_LIMIT = 0.4;
    public static final int LENGTH_FILTER_WINDOW = 100;
    public static final int TIME_BUDGET = 1000;

    AgentNeuralInterface<ChargeVariables> agent;
    AgentChargeNeural agentCasted;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironmentLambdas lambdas;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings envSettings;
    StateToValueFunctionContainerCharge container;
    AgentNeuralChargeTestHelper helper;
    MockedReplayBufferCreatorCharge bufferCreator;

    @BeforeEach
    public void init() {
        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(envSettings);
        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(0.1).momentum(0.1d)
                .nofNeuronsHidden(20).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(5)
                .valueNormalizer(new NormalizerMeanStd(List.of(envSettings.rewardBad()*10,0d,-1d,-2d,0d,-1d,0d)))
                //.valueNormalizer(new NormalizeMinMax(settings.rewardBad(),0))
                .build();

        agent = AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseValue(
                                agentSettings,
                                environmentCasted.getSettings(),
                                NORMALIZER_ONEDOTONE, VALUE_IF_NOT_OCCUPIED))
                .build();
        agentCasted = (AgentChargeNeural) agent;
        container= new StateToValueFunctionContainerCharge(lambdas, envSettings, SOC_LIMIT);
    }

    @Test
    @Tag("nettrain")
    public void givenFixedValue_whenTrain_thenCorrect() {

        helper=AgentNeuralChargeTestHelper.builder()
                .agent(agent).batchLength(BATCH_LENGTH).filterWindowLength(LENGTH_FILTER_WINDOW).build();
        helper.resetAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET);
        helper.plotAndSaveErrorHistory("fixed");

        bufferCreator= MockedReplayBufferCreatorCharge.builder().envSettings(envSettings).build();
        ChargeStateSuppliers suppliers=new ChargeStateSuppliers(envSettings);
        for (int i = 0; i < 10; i++) {
            ChargeState state = suppliers.stateRandomPosAndSoC();
            double valueLearned = agent.readValue(state);
            Assertions.assertEquals(container.fixedAtZero.apply(state),valueLearned, DELTA);
        }

    }

    @Test
    @Tag("nettrain")
    public void givenRuleBasedValue_whenTrain_thenCorrect() {
        bufferCreator= MockedReplayBufferCreatorCharge.builder()
                .bufferSize(BUFFER_SIZE).envSettings(envSettings).stateToValueFunction(container.limit)
                .build();
        ReplayBufferNStep<ChargeVariables> buffer=bufferCreator.createExpReplayBuffer();
        helper=AgentNeuralChargeTestHelper.builder()
                .agent(agent).nofIterations(NOF_ITERATIONS_RULE).iterationsBetweenPrints(ITERATIONS_BETWEEN_PRINTI)
                .batchLength(BATCH_LENGTH).filterWindowLength(LENGTH_FILTER_WINDOW).build();
        helper.trainAgent(buffer);
        helper.plotAndSaveErrorHistory("rule");
        Map<Integer, Pair<Double, Double>> valueMap = createValueMap(container.limit);
        printAndAsserValueMap(valueMap);

    }

    private static void printAndAsserValueMap(Map<Integer, Pair<Double, Double>> valueMap) {
        valueMap.keySet().forEach(i -> System.out.println("i = "+ i+" - "+valueMap.get(i)));
        for (Integer i: valueMap.keySet()) {
            Assertions.assertEquals(valueMap.get(i).getFirst(), valueMap.get(i).getSecond(),DELTA);
        }
    }

    @NotNull
    private Map<Integer, Pair<Double, Double>> createValueMap(Function<ChargeState, Double> stateToValueFunction) {
        Map<Integer, Pair<Double,Double>> valueMap=new HashMap<>();
        ChargeStateSuppliers suppliers=new ChargeStateSuppliers(envSettings);

        for (int i = 0; i < 10; i++) {
            ChargeState state = suppliers.stateRandomPosAndSoC();
            System.out.println("i = "+ i+" - " +"state = "+state.getVariables());
            double valueLearned = agent.readValue(state);
            valueMap.put(i, new Pair<>(stateToValueFunction.apply(state),valueLearned));
        }
        return valueMap;
    }




}