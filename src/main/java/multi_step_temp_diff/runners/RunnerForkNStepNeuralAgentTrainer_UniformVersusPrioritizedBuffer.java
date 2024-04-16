package multi_step_temp_diff.runners;

import common.list_arrays.ListUtils;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.prio_strategy.PrioritizationProportional;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepPrioritized;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.helpers_specific.ForkAgentFactory;
import multi_step_temp_diff.domain.helpers_specific.ForkHelper;
import multi_step_temp_diff.domain.helpers_specific.ForkTrainerFactory;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;

import java.util.*;

/***
 * Mostly errorsPrioAvg is smaller than errorsUniformAvg
 */

public class RunnerForkNStepNeuralAgentTrainer_UniformVersusPrioritizedBuffer {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 1;
    private static final int BATCH_SIZE = 30;
    private static final int NOF_EPIS = 100, MAX_TRAIN_TIME_IN_SEC = 55;
    private static final double DISCOUNT_FACTOR = 1;

    public static final double LEARNING_RATE = 1e-2;
    //private static final int INPUT_SIZE = ForkEnvironment.envSettings.nStates();
   public static final int NOF_HIDDEN_LAYERS = 2;

  //  NOF_NEURONS_HIDDEN = INPUT_SIZE;
    public static final double PROB_START = 0.1, PROB_END = 1e-5;
    public static final int NOF_SAMPLES = 10;

    static NStepNeuralAgentTrainer<ForkVariables> trainer;
    static AgentNeuralInterface<ForkVariables> agent;
    static ForkEnvironment environment;
    static AgentInfo<ForkVariables> agentInfo;

    public static void main(String[] args) {
        environment = new ForkEnvironment();
        List<Double> errorsUniform = new ArrayList<>();
        List<Double> errorsPrio = new ArrayList<>();
        ForkEnvironmentSettings envSettings=ForkEnvironmentSettings.getDefault();
        ForkAgentFactory agentFactory= ForkAgentFactory.builder()
                .environment(environment)
                .minOut(envSettings.rewardHell()).maxOut(envSettings.rewardHeaven())
                .learningRate(LEARNING_RATE).nofHiddenLayers(NOF_HIDDEN_LAYERS)
                .discountFactor(DISCOUNT_FACTOR)
                .build();

        ForkTrainerFactory trainerFactory= ForkTrainerFactory.builder()
                .agent(agent).environment(environment)
                .probStart(PROB_START).probEnd(PROB_END)
                .batchSize(BATCH_SIZE).nofEpis(NOF_EPIS)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .maxTrainingTimeInSeconds(MAX_TRAIN_TIME_IN_SEC).build();



        for (int i = 0; i < NOF_SAMPLES; i++) {
            //buildAgent();
            agent=agentFactory.buildAgent();
            trainerFactory.setAgent(agent);
            trainer=trainerFactory.buildTrainer(ReplayBufferNStepUniform.newDefault());
            trainer.train();
            ForkHelper helper=new ForkHelper(environment);
            helper.plotTdError(agent,"Error uniform");
            errorsUniform.add(getError());

            agent=agentFactory.buildAgent();
            trainerFactory.setAgent(agent);
            trainer=trainerFactory.buildTrainer(ReplayBufferNStepPrioritized.<ForkVariables>builder()
                    .alpha(0.5).beta0(1.0)
                    .prioritizationStrategy(new PrioritizationProportional<>(0.01))
                    .nofExperienceAddingBetweenProbabilitySetting(10)
                    .build());
            trainer.train();
            helper.plotTdError(agent,"Error prioritized");
            errorsPrio.add(getError());
        }


        double errorsUniformAvg = ListUtils.findAverage(errorsUniform).orElseThrow();
        System.out.println("errorsUniformAvg = " + errorsUniformAvg);

        double errorsPrioAvg = ListUtils.findAverage(errorsPrio).orElseThrow();
        System.out.println("errorsPrioAvg = " + errorsPrioAvg);

    }

    private static double getError() {
        agentInfo = new AgentInfo<>(agent);
        ForkHelper helper=new ForkHelper(environment);
        return helper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
    }





}
