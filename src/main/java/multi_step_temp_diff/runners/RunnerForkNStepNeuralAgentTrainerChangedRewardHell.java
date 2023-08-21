package multi_step_temp_diff.runners;

import common.MathUtils;
import multi_step_temp_diff.domain.agent_parts.PrioritizationProportional;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.helpers_common.StateValuePrinter;
import multi_step_temp_diff.domain.helpers_specific.ForkAgentFactory;
import multi_step_temp_diff.domain.helpers_specific.ForkHelper;
import multi_step_temp_diff.domain.helpers_specific.ForkTrainerFactory;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import plotters.PlotterMultiplePanelsTrajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunnerForkNStepNeuralAgentTrainerChangedRewardHell {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 1;
    private static final int BATCH_SIZE = 30;
    private static final int NOF_EPIS = 1000, MAX_TRAIN_TIME_IN_SEC = 55;
    private static final int LENGTH_WINDOW = 1000;
    private static final double DISCOUNT_FACTOR = 1;

    public static final double LEARNING_RATE = 1e-2;
    public static final int NOF_HIDDEN_LAYERS = 2;

    //  NOF_NEURONS_HIDDEN = INPUT_SIZE;
    public static final double PROB_START = 0.1, PROB_END = 1e-5;
    public static final int MAX_VALUE_IN_PLOT = 5;
    public static final int NOF_SAMPLES = 10;

    static ForkEnvironment environment;

    public static void main(String[] args) {
        ForkEnvironmentSettings envSettings =ForkEnvironmentSettings.getDefault();
        envSettings=envSettings.getWithRewardHell(envSettings.rewardHeaven());
        environment = new ForkEnvironment(envSettings);

        environment.setEnvSettings(envSettings);
        ForkAgentFactory agentFactory= ForkAgentFactory.builder()
                .environment(environment)
                .minOut(envSettings.rewardHell()).maxOut(envSettings.rewardHeaven())
                .learningRate(LEARNING_RATE).nofHiddenLayers(NOF_HIDDEN_LAYERS)
                .discountFactor(DISCOUNT_FACTOR)
                .build();
        AgentForkNeural agent =agentFactory.buildAgent();

        ForkTrainerFactory trainerFactory= ForkTrainerFactory.builder()
                .agent(agent).environment(environment)
                .probStart(PROB_START).probEnd(PROB_END)
                .batchSize(BATCH_SIZE).nofEpis(NOF_EPIS)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .maxTrainingTimeInSeconds(MAX_TRAIN_TIME_IN_SEC).build();

        NStepNeuralAgentTrainer<ForkVariables> trainer=trainerFactory.buildTrainer(ReplayBufferNStepUniform.newDefault());

        trainer.train();

        ForkHelper helper=new ForkHelper(environment);
        helper.plotTdError(agent,"TD error");

        StateValuePrinter<ForkVariables> stateValuePrinter=new StateValuePrinter<>(agent,environment);
        stateValuePrinter.printStateValues();

    }




}
