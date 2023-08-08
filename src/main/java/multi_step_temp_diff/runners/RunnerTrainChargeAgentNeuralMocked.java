package multi_step_temp_diff.runners;

import common.MovingAverage;
import common.MultiplePanelsPlotter;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_parts.ValueTracker;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingOneAtOccupiedSoCsSeparate;
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
import multi_step_temp_diff.domain.test_helpers.StateToValueFunctionContainerCharge;
import org.neuroph.util.TransferFunctionType;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class RunnerTrainChargeAgentNeuralMocked {

    private static final int BUFFER_SIZE = 10_000, NOF_ITERATIONS = 10_000;
    private static final int BATCH_LENGTH = 100;
    public static final String PICS_FOLDER = "pics/";
    public static final int ITERATIONS_BETWEEN_PRINTI = 1000;

    public static final NormalizerMeanStd NORMALIZER_MINUSONE =
            new NormalizerMeanStd(List.of(-1d,-1d,-1d,-1d,0.3, 0.5, 0.7, 0.9,1.0));
    public static final NormalizerMeanStd NORMALIZER_ZERO =
            new NormalizerMeanStd(List.of(0d,0d,0d,0d,0.3, 0.5, 0.7, 0.9,1.0));
    public static final NormalizerMeanStd NORMALIZER_ONE =
            new NormalizerMeanStd(List.of(0.3, 0.5, 0.7, 0.9,1.0,1.0,1.0,1.0));
    public static final NormalizerMeanStd NORMALIZER_TWO =
            new NormalizerMeanStd(List.of(0.3, 0.5, 0.7, 0.9, 2d, 2d, 2d, 2d, 2d));
    public static final NormalizerMeanStd NORMALIZER_ONEDOTONE =
            new NormalizerMeanStd(List.of(0.3, 0.5, 0.7, 0.9, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d));
    public static final NormalizerMeanStd NORMALIZER_SEPARATE_SOCS =
            new NormalizerMeanStd(List.of(1.0, 0d, 0d, 0d, 0d, 0d, 0.6d, 0d, 0d));
    public static final double SOC_LMIIT = 0.4;
    public static final int LENGTH_FILTER_WINDOW = 100;


    static AgentNeuralInterface<ChargeVariables> agent;
    static AgentChargeNeural agentCasted;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings envSettings;
    static ChargeState initState;
    static AgentChargeNeuralSettings agentSettings;
    static StateToValueFunctionContainerCharge container;

    public static void main(String[] args) throws InterruptedException {

        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(envSettings);
        initState = new ChargeState(ChargeVariables.builder().build());
        agentSettings = getAgentSettings();
        container= new StateToValueFunctionContainerCharge(lambdas,envSettings, SOC_LMIIT);

        MockedReplayBufferCreatorCharge bufferCreator= MockedReplayBufferCreatorCharge.builder()
                .bufferSize(BUFFER_SIZE).settings(envSettings).stateToValueFunction(container.limit)
                .build();
        ReplayBufferNStep<ChargeVariables> expBuffer=bufferCreator.createExpReplayBuffer();

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_MINUSONE,-1d));
  //      trainAgent(expBuffer);
        plotAndSaveErrorHistory("MinusOne");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_ZERO,0d));
    //    trainAgent(expBuffer);
        plotAndSaveErrorHistory("Zero");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_ONE,1d));
    //    trainAgent(expBuffer);
        plotAndSaveErrorHistory("One");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_TWO,2d));
       // trainAgent(expBuffer);
        plotAndSaveErrorHistory("Two");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_ONEDOTONE,1.1d));
        trainAgent(expBuffer);
        plotAndSaveErrorHistory("OneDotOne");

        agent = createAgent(new HotEncodingOneAtOccupiedSoCsSeparate(agentSettings, envSettings, NORMALIZER_SEPARATE_SOCS));
       // trainAgent(expBuffer);
        plotAndSaveErrorHistory("Sep socs");


    }

    private static AgentChargeNeuralSettings getAgentSettings() {
        return AgentChargeNeuralSettings.builder()
                .learningRate(0.5)
                .nofNeuronsHidden(20).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(3)
                .valueNormalizer(new NormalizerMeanStd(List.of(envSettings.rewardBad() * 10, 0d, -1d, -2d, 0d, -1d, 0d)))
                .build();
    }

    private static AgentChargeNeural createAgent(InputVectorSetterChargeInterface inputSetter) {
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
        MovingAverage movingAverage=new MovingAverage(
                LENGTH_FILTER_WINDOW,errorTracker.getValueHistoryAbsoluteValues());
        plotter.plot(List.of(movingAverage.getFiltered()));
        Thread.sleep(1000);
        plotter.saveImage(PICS_FOLDER +fileName);
    }

}
