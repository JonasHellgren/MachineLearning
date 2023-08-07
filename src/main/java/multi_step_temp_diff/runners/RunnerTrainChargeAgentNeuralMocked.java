package multi_step_temp_diff.runners;

import common.MultiplePanelsPlotter;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_parts.ValueTracker;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputVectorSetterChargeInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers.MockedReplayBufferCreatorCharge;
import multi_step_temp_diff.domain.normalizer.NormalizerMeanStd;
import org.neuroph.util.TransferFunctionType;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class RunnerTrainChargeAgentNeuralMocked {

    private static final int BUFFER_SIZE = 10_000, NOF_ITERATIONS = 5_00;
    private static final int BATCH_LENGTH = 100;
    public static final String PICS_FOLDER = "pics/";
    public static final int ITERATIONS_BETWEEN_PRINTI = 1000;

    public static final NormalizerMeanStd NORMALIZER_MINUSONE = new NormalizerMeanStd(List.of(-1d,-1d,-1d,-1d,0.3, 0.5, 0.7, 0.9,1.0));
    public static final NormalizerMeanStd NORMALIZER_ZERO = new NormalizerMeanStd(List.of(0d,0d,0d,0d,0.3, 0.5, 0.7, 0.9,1.0));
    public static final NormalizerMeanStd NORMALIZER_ONE = new NormalizerMeanStd(List.of(0.3, 0.5, 0.7, 0.9,1.0,1.0,1.0,1.0));
    public static final NormalizerMeanStd NORMALIZER_TWO = new NormalizerMeanStd(List.of(0.3, 0.5, 0.7, 0.9, 2d, 2d, 2d, 2d, 2d));
    public static final NormalizerMeanStd NORMALIZER_ONEDOTONE = new NormalizerMeanStd(List.of(0.3, 0.5, 0.7, 0.9, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d));


    static AgentNeuralInterface<ChargeVariables> agent;
    static AgentChargeNeural agentCasted;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings settings;

    static Function<ChargeState, Double> stateToValueFunction=(s) -> {
        double socLimit = 0.4;
        ChargeVariables vars = s.getVariables();
        BiPredicate<Double,Integer> isBelowSocLimitAndNotChargePos=(soc, pos) ->
                soc<socLimit && !lambdas.isChargePos.test(pos);

        return  (isBelowSocLimitAndNotChargePos.test(vars.socA, vars.posA) ||
                isBelowSocLimitAndNotChargePos.test(vars.socB, vars.posB))
                ? settings.rewardBad()
                :0d;
    };

    public static void main(String[] args) throws InterruptedException {

        settings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(settings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(settings);
        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        AgentChargeNeuralSettings agentSettings = getAgentSettings();
        MockedReplayBufferCreatorCharge bufferCreator= MockedReplayBufferCreatorCharge.builder()
                .bufferSize(BUFFER_SIZE).settings(settings).stateToValueFunction(stateToValueFunction)
                .build();
        ReplayBufferNStep<ChargeVariables> expBuffer=bufferCreator.createExpReplayBuffer();

        agent = createAgent(initState, agentSettings,
                new HotEncodingSoCAtOccupiedElseValue(agentSettings,settings, NORMALIZER_MINUSONE,-1d));
        trainAgent(expBuffer);
        plotAndSaveErrorHistory("MinusOne");

        agent = createAgent(initState, agentSettings,
                new HotEncodingSoCAtOccupiedElseValue(agentSettings,settings, NORMALIZER_ZERO,0d));
        trainAgent(expBuffer);
        plotAndSaveErrorHistory("Zero");

        agent = createAgent(initState, agentSettings,
                new HotEncodingSoCAtOccupiedElseValue(agentSettings,settings, NORMALIZER_ONE,1d));
        trainAgent(expBuffer);
        plotAndSaveErrorHistory("One");

        agent = createAgent(initState, agentSettings,
                new HotEncodingSoCAtOccupiedElseValue(agentSettings,settings, NORMALIZER_TWO,2d));
        trainAgent(expBuffer);
        plotAndSaveErrorHistory("Two");

        agent = createAgent(initState, agentSettings,
                new HotEncodingSoCAtOccupiedElseValue(agentSettings,settings, NORMALIZER_ONEDOTONE,1.1d));
        trainAgent(expBuffer);
        plotAndSaveErrorHistory("OneDotOne");



    }

    private static AgentChargeNeuralSettings getAgentSettings() {
        return AgentChargeNeuralSettings.builder()
                .learningRate(0.1)
                .nofNeuronsHidden(20).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(3)
                .valueNormalizer(new NormalizerMeanStd(List.of(settings.rewardBad() * 10, 0d, -1d, -2d, 0d, -1d, 0d)))
                .build();
    }

    private static AgentChargeNeural createAgent(ChargeState initState,
                                                 AgentChargeNeuralSettings agentSettings,
                                                 InputVectorSetterChargeInterface inputSetter) {
        return AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(inputSetter)
                .build();
    }

    private static void trainAgent(ReplayBufferNStep<ChargeVariables> buffer) {
        for (int i = 0; i < NOF_ITERATIONS; i++) {
            if(i % ITERATIONS_BETWEEN_PRINTI ==0)
                System.out.println("i = " + i);
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }
    }

    private static void plotAndSaveErrorHistory(String fileName) throws InterruptedException {
        agentCasted = (AgentChargeNeural) agent;
        ValueTracker errorTracker=agentCasted.getErrorHistory();
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(List.of("Error "+fileName),"iter");
        plotter.plot(List.of(errorTracker.getValueHistoryAbsoluteValues()));
        Thread.sleep(1000);
        plotter.saveImage(PICS_FOLDER +fileName);
    }

}
