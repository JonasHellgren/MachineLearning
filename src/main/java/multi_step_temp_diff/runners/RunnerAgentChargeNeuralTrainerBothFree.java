package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStepUniform;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.factories.TrainerFactory;
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;

/***

 */

public class RunnerAgentChargeNeuralTrainerBothFree {

    private static final int NOF_EPIS = 1000, MAX_TRAIN_TIME_IN_MINUTES = 30;  //one will limit
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    public static final int BATCH_SIZE1 = 30;
    public static final int NOF_LAYERS_HIDDEN = 10;
    public static final int NOF_NEURONS_HIDDEN = 15;

    static AgentNeuralInterface<ChargeVariables> agent;
    static NStepNeuralAgentTrainer<ChargeVariables> trainer;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings envSettingsForTraining;

    public static void main(String[] args) {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        envSettingsForTraining = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_TRAINING);
        environment = new ChargeEnvironment(envSettingsForTraining);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas = new ChargeEnvironmentLambdas(envSettingsForTraining);
        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(envSettingsForTraining);


        ChargeAgentFactory agentFactory = ChargeAgentFactory.builder()
                .environment(environment).envSettings(envSettings)
                .build();
        agent = agentFactory.buildAgent(ChargeState.newDummy(), NOF_LAYERS_HIDDEN, NOF_NEURONS_HIDDEN);

        TrainerFactory<ChargeVariables> trainerFactory = TrainerFactory.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .batchSize(BATCH_SIZE1).startEndProb(START_END_PROB)
                .nofEpis(NOF_EPIS).maxTrainingTimeInMilliS(1000 * 60 * MAX_TRAIN_TIME_IN_MINUTES)
                .nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                .buffer(ReplayBufferNStepUniform.newFromMaxSize(MAX_BUFFER_SIZE_EXPERIENCE_REPLAY))
                .build();
        trainer = trainerFactory.buildTrainer();
        trainer.train();
        ChargePlotHelper plotHelper=new ChargePlotHelper(agent,trainer);
        plotHelper.doMultiplePlots(envSettings);

//        doPlotting(envSettings);
        agent.saveMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);

        ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newAllScenarios(environment, agent);
        evaluator.evaluate();
        out.println("evaluator = " + evaluator);

    }
/*

    private static void doPlotting(ChargeEnvironmentSettings envSettings) {
        ChargePlotHelper plotHelper = new ChargePlotHelper(agent, trainer);
        plotHelper.plotTdError();
        plotHelper.plotSumRewardsTracker();
        int posB = 0;
        double socB = 1.0;
        plotHelper.createScatterPlot(envSettings, "V-30", 0.3, posB);
        plotHelper.createScatterPlot(envSettings, "V-80", 0.8, posB);
        plotHelper.plotV20MinusV11VersusSoC(posB, socB);
    }

*/

}
