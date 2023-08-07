package multi_step_td.charge;

import common.Conditionals;
import common.MultiplePanelsPlotter;
import common.RandUtils;
import lombok.SneakyThrows;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_parts.ValueTracker;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseZero;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.normalizer.NormalizeMinMax;
import multi_step_temp_diff.domain.normalizer.NormalizerMeanStd;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/***
 * MeanStd normalizer gives massive improvement versus MinMax
 */

public class TestAgentNeuralChargeMockedData {

    private static final int BUFFER_SIZE = 10_000, NOF_ITERATIONS = 5_00;
    private static final int BATCH_LENGTH = 100;
    public static final double DELTA = 3;
    public static final String PICS_FOLDER = "pics/";
    public static final int ITERATIONS_BETWEEN_PRINTI = 1000;

    AgentNeuralInterface<ChargeVariables> agent;
    AgentChargeNeural agentCasted;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironmentLambdas lambdas;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings settings;
    int nofSiteNodes;

    @BeforeEach
    public void init() {
        settings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(settings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(settings);
        nofSiteNodes = environmentCasted.getSettings().siteNodes().size();
        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(0.2)
                .nofNeuronsHidden(10).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(5)
                .valueNormalizer(new NormalizerMeanStd(List.of(settings.rewardBad()*10,0d,-1d,-2d,0d,-1d,0d)))
                //.valueNormalizer(new NormalizeMinMax(settings.rewardBad(),0))
                .build();

        agent = AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseZero(
                                agentSettings,
                                environmentCasted.getSettings(),
                                //new NormalizeMinMax(0,1)))
                                new NormalizerMeanStd(List.of(0.3,0.5,0.7,0.9))))
                .build();
        agentCasted = (AgentChargeNeural) agent;
    }

    @Test
    @Tag("nettrain")
    public void givenFixedValue_whenTrain_thenCorrect() {
        Function<ChargeState, Double> stateToValueFunction =(s) -> -0d;
        ReplayBufferNStep<ChargeVariables> buffer = createExpReplayBuffer(stateToValueFunction);
        trainAgent(buffer);
        plotAndSaveErrorHistory("fixed");

        for (int i = 0; i < 10; i++) {
            ChargeState state = stateRandomPosAndSoC();
            double valueLearned = agent.readValue(state);
            Assertions.assertEquals(stateToValueFunction.apply(state),valueLearned, DELTA);
        }

    }

    @Test
    @Tag("nettrain")
    public void givenRuleBasedValue_whenTrain_thenCorrect() {

        Function<ChargeState, Double> stateToValueFunction=(s) -> {
            double socLimit = 0.4;
            ChargeVariables vars = s.getVariables();
            BiPredicate<Double,Integer> isBelowSocLimitAndNotChargePos=(soc, pos) ->
                    soc<socLimit && !lambdas.isChargePos.test(pos);

            return  (isBelowSocLimitAndNotChargePos.test(vars.socA, vars.posA) ||
                    isBelowSocLimitAndNotChargePos.test(vars.socB, vars.posB))
                    ? settings.rewardBad()
                    :0d;
        };

        ReplayBufferNStep<ChargeVariables> buffer = createExpReplayBuffer(stateToValueFunction);
        trainAgent(buffer);
        plotAndSaveErrorHistory("rule");
        Map<Integer, Pair<Double, Double>> valueMap = createValueMap(stateToValueFunction);
        printAndAsserValueMap(valueMap);

    }

    private static void printAndAsserValueMap(Map<Integer, Pair<Double, Double>> valueMap) {
        valueMap.keySet().forEach(i -> System.out.println(valueMap.get(i)));
        for (Integer i: valueMap.keySet()) {
            Assertions.assertEquals(valueMap.get(i).getFirst(), valueMap.get(i).getSecond(),DELTA);
        }
    }

    @NotNull
    private Map<Integer, Pair<Double, Double>> createValueMap(Function<ChargeState, Double> stateToValueFunction) {
        Map<Integer, Pair<Double,Double>> valueMap=new HashMap<>();
        for (int i = 0; i < 10; i++) {
            ChargeState state = stateRandomPosAndSoC();
            double valueLearned = agent.readValue(state);
            valueMap.put(i, new Pair<>(stateToValueFunction.apply(state),valueLearned));
        }
        return valueMap;
    }

    private ReplayBufferNStep<ChargeVariables> createExpReplayBuffer(Function<ChargeState, Double> stateToValueFunction) {
        return ReplayBufferNStep.<ChargeVariables>builder()
                .buffer(createBuffer(stateToValueFunction)).build();
    }

    private void trainAgent(ReplayBufferNStep<ChargeVariables> buffer) {
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            if(i % ITERATIONS_BETWEEN_PRINTI ==0)
                System.out.println("i = " + i);
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }
    }


    @NotNull
    private List<NstepExperience<ChargeVariables>> createBuffer(Function<ChargeState, Double> stateFunction) {
        List<NstepExperience<ChargeVariables>> batch = new ArrayList<>();
        for (int i = 0; i < BUFFER_SIZE; i++) {
            ChargeState state = stateRandomPosAndSoC();
            NstepExperience<ChargeVariables> exp = NstepExperience.<ChargeVariables>builder()
                    .stateToUpdate(state)
                    .value(stateFunction.apply(state))
                    .build();
            batch.add(exp);
        }
        return batch;
    }


    @SneakyThrows
    private void plotAndSaveErrorHistory(String fileName) {
        ValueTracker errorTracker=agentCasted.getErrorHistory();
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(List.of("Error"),"iter");
        plotter.plot(List.of(errorTracker.getValueHistoryAbsoluteValues()));
        Thread.sleep(1000);
        plotter.saveImage(PICS_FOLDER +fileName);
    }

    @NotNull
    private ChargeState stateRandomPosAndSoC() {
        return new ChargeState(ChargeVariables.builder()
                .posA(randomPos()).posB(randomPos())
                .socA(randomSoC()).socB(randomSoC())
                .build());
    }

    private double randomSoC() {
        return RandUtils.getRandomDouble(0, 1);
    }

    private int randomPos() {
       RandUtils<Integer> randUtils=new RandUtils<>();
        List<Integer> es = new ArrayList<>(settings.siteNodes());
        return randUtils.getRandomItemFromList(es);
    }


}