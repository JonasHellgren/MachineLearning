package multi_step_temp_diff.runners;

import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
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
import multi_step_temp_diff.domain.helpers_specific.ChargeMockedReplayBufferCreator;
import multi_step_temp_diff.domain.helpers_specific.ChargePlotHelper;
import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargeStateToValueFunctionContainer;
import org.neuroph.util.TransferFunctionType;
import java.util.List;

@Log
public class RunnerTrainChargeAgentNeuralMocked {

    private static final int BUFFER_SIZE = 10_000, NOF_ITERATIONS = 5_000;
    private static final int BATCH_LENGTH = 100;
    public static final int ITERATIONS_BETWEEN_PRINTI = 1000;
    public static final double SOC_LMIIT = 0.4;
    public static final int LENGTH_FILTER_WINDOW = 100;
    public static final TransferFunctionType TRANSFER_FUNCTION_TYPE = TransferFunctionType.SIGMOID;

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


    static AgentNeuralInterface<ChargeVariables> agent;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings envSettings;
    static ChargeState initState;
    static AgentChargeNeuralSettings agentSettings;
    static ChargeStateToValueFunctionContainer container;
    static ChargeAgentNeuralHelper helper;
    static ChargePlotHelper plotHelper;

    public static void main(String[] args) throws InterruptedException {

        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(envSettings);
        initState = new ChargeState(ChargeVariables.builder().build());
        agentSettings = getAgentSettings();
        container= new ChargeStateToValueFunctionContainer(lambdas,envSettings, SOC_LMIIT);
        ChargeMockedReplayBufferCreator bufferCreator= ChargeMockedReplayBufferCreator.builder()
                .bufferSize(BUFFER_SIZE).envSettings(envSettings).stateToValueFunction(container.limit)
                .build();
        ReplayBufferNStepUniform<ChargeVariables> expBuffer=bufferCreator.createExpReplayBuffer();
        plotHelper= new ChargePlotHelper(agent,null);

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_MINUSONE,-1d));
        helper= crateHelper();
        helper.trainAgent(expBuffer);
        plotHelper.plotAndSaveErrorHistory("MinusOne");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_ZERO,0d));
        helper= crateHelper();
        helper.trainAgent(expBuffer);
        plotHelper.plotAndSaveErrorHistory("Zero");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_ONE,1d));
        helper= crateHelper();
        helper.trainAgent(expBuffer);
        plotHelper.plotAndSaveErrorHistory("One");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_TWO,2d));
        helper= crateHelper();
        helper.trainAgent(expBuffer);
        plotHelper.plotAndSaveErrorHistory("Two");

        agent = createAgent(new HotEncodingSoCAtOccupiedElseValue(agentSettings, envSettings, NORMALIZER_ONEDOTONE,1.1d));
        helper= crateHelper();

        helper.trainAgent(expBuffer);
        plotHelper.plotAndSaveErrorHistory("OneDotOne");

        agent = createAgent(new HotEncodingOneAtOccupiedSoCsSeparate(agentSettings, envSettings, NORMALIZER_SEPARATE_SOCS));
        helper= crateHelper();
        helper.trainAgent(expBuffer);
        plotHelper.plotAndSaveErrorHistory("Sep socs");

        log.info("Running finished");

    }

    private static ChargeAgentNeuralHelper crateHelper() {
        return ChargeAgentNeuralHelper.builder()
                .agent(agent).nofIterations(NOF_ITERATIONS).iterationsBetweenPrints(ITERATIONS_BETWEEN_PRINTI)
                .batchLength(BATCH_LENGTH).filterWindowLength(LENGTH_FILTER_WINDOW).build();
    }

    private static AgentChargeNeuralSettings getAgentSettings() {
        return AgentChargeNeuralSettings.builder()
                .learningRate(0.1)
                .nofNeuronsHidden(20).transferFunctionType(TRANSFER_FUNCTION_TYPE)
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



}
