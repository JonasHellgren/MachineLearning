package multi_step_temp_diff.runners;

import common.ListUtils;
import common.MathUtils;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agent_parts.PrioritizationProportional;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.agent_valueobj.NetSettings;
import multi_step_temp_diff.domain.helpers_specific.ForkAndMazeHelper;
import org.neuroph.util.TransferFunctionType;
import plotters.PlotterMultiplePanelsTrajectory;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/***
 * Mostly errorsPrioAvg is smaller than errorsUniformAvg
 */

public class RunnerForkNStepNeuralAgentTrainer_UniformVersusPrioritizedBuffer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 1;
    private static final int BATCH_SIZE = 30;
    private static final int NOF_EPIS = 100, MAX_TRAIN_TIME_IN_SEC = 55;
    private static final int LENGTH_WINDOW = 1000;
    private static final int DISCOUNT_FACTOR = 1;

    public static final double LEARNING_RATE = 1e-2;
    private static final int INPUT_SIZE = ForkEnvironment.envSettings.nofStates();
    public static final int NOF_HIDDEN_LAYERS = 2, NOF_NEURONS_HIDDEN = INPUT_SIZE;
    public static final double PROB_START = 0.1, PROB_END = 1e-5;
    public static final int MAX_VALUE_IN_PLOT = 5;
    public static final int NOF_SAMPLES = 10;

    static NStepNeuralAgentTrainer<ForkVariables> trainer;
    static AgentNeuralInterface<ForkVariables> agent;
    static AgentForkNeural agentCasted;
    static ForkEnvironment environment;
    static AgentInfo<ForkVariables> agentInfo;

    public static void main(String[] args) {
        environment = new ForkEnvironment();
        List<Double> errorsUniform = new ArrayList<>();
        List<Double> errorsPrio = new ArrayList<>();

        for (int i = 0; i < NOF_SAMPLES; i++) {
            buildAgent();
            buildTrainer(ReplayBufferNStepUniform.newDefault());
            trainer.train();
            plotTdError("Error uniform");
            errorsUniform.add(getError());

            buildAgent();
            buildTrainer(ReplayBufferNStepPrioritized.<ForkVariables>builder()
                    .alpha(0.5).beta0(1.0)
                    .prioritizationStrategy(new PrioritizationProportional<>(0.01))
                    .nofExperienceAddingBetweenProbabilitySetting(10)
                    .build());
            trainer.train();
            plotTdError("Error prioritized");
            errorsPrio.add(getError());
        }


        double errorsUniformAvg = ListUtils.findAverage(errorsUniform).orElseThrow();
        System.out.println("errorsUniformAvg = " + errorsUniformAvg);

        double errorsPrioAvg = ListUtils.findAverage(errorsPrio).orElseThrow();
        System.out.println("errorsPrioAvg = " + errorsPrioAvg);

    }

    private static double getError() {
        agentInfo = new AgentInfo<>(agent);
        return ForkAndMazeHelper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
    }

    static void plotTdError(String yTitle) {
        AgentInfo<ForkVariables> agentInfo = new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories = new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        List<Double> filteredAndClipped = filtered1.stream().mapToDouble(n -> MathUtils.clip(n, 0, MAX_VALUE_IN_PLOT)).boxed().toList();
        listOfTrajectories.add(filteredAndClipped);
        PlotterMultiplePanelsTrajectory plotter = new PlotterMultiplePanelsTrajectory(Collections.singletonList(yTitle), "Step");
        plotter.plot(listOfTrajectories);
    }

    static private void buildAgent() {
        double minOut = ForkEnvironment.envSettings.rewardHell();
        double maxOut = ForkEnvironment.envSettings.rewardHeaven();
        NetSettings netSettings = NetSettings.builder()
                .learningRate(LEARNING_RATE)
                .inputSize(INPUT_SIZE).nofHiddenLayers(NOF_HIDDEN_LAYERS).nofNeuronsHidden(NOF_NEURONS_HIDDEN)
                .transferFunctionType(TransferFunctionType.TANH)
                .minOut(minOut).maxOut(maxOut)
                .normalizer(new NormalizerMeanStd(List.of(10 * minOut, 10 * maxOut, 0d, 0d, 0d))).build();
        //        .normalizer(new NormalizeMinMax(minOut*2,maxOut*2)).build();  //also works

        agent = AgentForkNeural.newWithDiscountFactorAndMemorySettings(
                environment, DISCOUNT_FACTOR, netSettings);
    }

    public static void buildTrainer(ReplayBufferInterface<ForkVariables> buffer) {
        agentCasted = (AgentForkNeural) agent;
        NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.builder()
                .probStart(PROB_START).probEnd(PROB_END).nofIterations(1)
                .batchSize(BATCH_SIZE)
                .nofEpis(NOF_EPIS)
                .maxTrainingTimeInMilliS(1000 * MAX_TRAIN_TIME_IN_SEC)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .build();

        trainer = NStepNeuralAgentTrainer.<ForkVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> ForkState.newFromRandomPos())
                .agentNeural(agent)
                .environment(environment)
                .buffer(buffer)
                .build();

        System.out.println("buildTrainer");
    }


}
