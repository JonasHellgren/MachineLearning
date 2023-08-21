package multi_step_temp_diff.runners;

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

/***
 * Insights:
 * defaultavgError minskar för default med ökande NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED=N
 * - Backup från framförvarande "gissat" value minskar/fösrvinner med stor N

 * defaultavgError ökar för default med ökande PROB_START
 * - Fler felaktiga experiences i buffer, tar hell från tidig state
 *
 * defaultavgError typ noll när reward hell equal to heaven
 *
 */

public class RunnerForkNStepNeuralAgentTrainerChangedRewardHell {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    private static final int BATCH_SIZE = 30;
    private static final int NOF_EPIS = 1000, MAX_TRAIN_TIME_IN_SEC = 55;
    private static final double DISCOUNT_FACTOR = 1;
    public static final double LEARNING_RATE = 1e-2;
    public static final int NOF_HIDDEN_LAYERS = 2;
    public static final double PROB_START = 0.1, PROB_END = 1e-5;

    static ForkEnvironment environment;

    public static void main(String[] args) {
        ForkEnvironmentSettings envSettings =ForkEnvironmentSettings.getDefault();
        environment = new ForkEnvironment(envSettings);

        String type = "- default";
        environment.setEnvSettings(envSettings);
        AgentForkNeural agent = getAgent(envSettings);
        NStepNeuralAgentTrainer<ForkVariables> trainer = getTrainer(agent);
        trainer.train();
        plotAndPrintStatesAndAverageValueError(agent, type);

        type = "- reward hell equal to heaven";
        environment.setEnvSettings(envSettings.getWithRewardHell(envSettings.rewardHeaven()));
        agent = getAgent(envSettings);
        trainer = getTrainer(agent);
        trainer.train();
        plotAndPrintStatesAndAverageValueError(agent,type);
    }

    private static void plotAndPrintStatesAndAverageValueError(AgentForkNeural agent, String type) {
        StateValuePrinter<ForkVariables> stateValuePrinter=new StateValuePrinter<>(agent,environment);
        stateValuePrinter.printStateValues();
        AgentInfo<ForkVariables> agentInfo=new AgentInfo<>(agent);
        ForkHelper helper=new ForkHelper(environment);

        helper.plotTdError(agent,"TD error"+ type);

        double avgError = helper.avgErrorFork(agentInfo.stateValueMap(environment.stateSet()));
        System.out.println(type+"avgError = " + avgError);
    }

    private static NStepNeuralAgentTrainer<ForkVariables> getTrainer(AgentForkNeural agent) {
        ForkTrainerFactory trainerFactory= ForkTrainerFactory.builder()
                .agent(agent).environment(environment)
                .probStart(PROB_START).probEnd(PROB_END)
                .batchSize(BATCH_SIZE).nofEpis(NOF_EPIS)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .maxTrainingTimeInSeconds(MAX_TRAIN_TIME_IN_SEC).build();

        return trainerFactory.buildTrainer(ReplayBufferNStepUniform.newDefault());
    }

    private static AgentForkNeural getAgent(ForkEnvironmentSettings envSettings) {
        ForkAgentFactory agentFactory= ForkAgentFactory.builder()
                .environment(environment)
                .minOut(envSettings.rewardHell()).maxOut(envSettings.rewardHeaven())
                .learningRate(LEARNING_RATE).nofHiddenLayers(NOF_HIDDEN_LAYERS)
                .discountFactor(DISCOUNT_FACTOR)
                .build();
        return agentFactory.buildAgent();
    }


}
