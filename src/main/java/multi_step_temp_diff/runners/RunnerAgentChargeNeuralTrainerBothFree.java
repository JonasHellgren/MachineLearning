package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy.RemoveStrategyOldest;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.agent_parts.replay_buffer.remove_strategy.RemoveStrategyRandom;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.factories.TrainerFactory;
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.apache.commons.lang3.tuple.Pair;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;

/***

 */

public class RunnerAgentChargeNeuralTrainerBothFree {

    private static final int NOF_EPIS = 100_000, MAX_TRAIN_TIME_IN_MINUTES = 2;  //one will limit
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    public static final int BATCH_SIZE1 = 30;
    public static final int NOF_LAYERS_HIDDEN = 3;  //10
    public static final int NOF_NEURONS_HIDDEN = 15;  //15
    public static final int MAX_BUFFER_SIZE = 10_000;
    public static final int MAX_NOF_STEPS_IN_EPIS = 10_000;
    public static final Pair<Double, Double> START_END_PROB = Pair.of(0.9, 0.01);
    public static final double LEARNING_RATE1 = 0.01, DISCOUNT_FACTOR = 0.95;

    static EnvironmentInterface<ChargeVariables> environment;

    public static void main(String[] args) {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        var envSettingsForTraining = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_IN_EPIS);
        environment = new ChargeEnvironment(envSettingsForTraining);
        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(envSettingsForTraining);

        ChargeAgentFactory agentFactory = ChargeAgentFactory.builder()
                .environment(environment).envSettings(envSettings)
                .discountFactor(DISCOUNT_FACTOR).learningRate(LEARNING_RATE1)
                .build();
        var agent = agentFactory.buildAgent(ChargeState.newDummy(), NOF_LAYERS_HIDDEN, NOF_NEURONS_HIDDEN);

        var trainerFactory = TrainerFactory.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .batchSize(BATCH_SIZE1).startEndProb(START_END_PROB)
                .nofEpis(NOF_EPIS).maxTrainingTimeInMilliS(1000 * 60 * MAX_TRAIN_TIME_IN_MINUTES)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                .buffer(ReplayBufferNStepUniform.<ChargeVariables>builder()
                        .maxSize(MAX_BUFFER_SIZE).removeStrategy(new RemoveStrategyRandom<>())
                        .build())
                .build();
        var trainer = trainerFactory.buildTrainer();
        trainer.train();
        ChargePlotHelper plotHelper=new ChargePlotHelper(agent,trainer);
        plotHelper.doMultiplePlots(envSettings);

        agent.saveMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);

        var evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, agent);
        evaluator.evaluate();
        out.println("evaluator = " + evaluator);


    }


}
