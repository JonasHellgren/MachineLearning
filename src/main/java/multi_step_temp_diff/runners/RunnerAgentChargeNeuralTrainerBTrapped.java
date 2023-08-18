package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.factories.TrainerFactory;
import multi_step_temp_diff.domain.helpers_specific.*;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;


/**No evidence reset helps
 *
 *
 */

@Log
public class RunnerAgentChargeNeuralTrainerBTrapped {

    private static final int NOF_EPIS = 50;
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    public static final int POS_B = TRAP_POS; //trap
    public static final double SOC_B = 1.0;
    public static final int MAX_NOF_STEPS_TRAINING = 200;
    public static final int NOF_LAYERS_HIDDEN = 10;
    public static final int NOF_NEURONS_HIDDEN = 15;

    static AgentNeuralInterface<ChargeVariables> agent;
    static NStepNeuralAgentTrainer<ChargeVariables> trainer;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings envSettings;

    public static void main(String[] args) {
        ChargeEnvironmentSettings envSettings = ChargeEnvironmentSettings.newDefault();
        RunnerAgentChargeNeuralTrainerBTrapped.envSettings = envSettings.copyWithNewMaxNofSteps(MAX_NOF_STEPS_TRAINING);
        environment = new ChargeEnvironment(RunnerAgentChargeNeuralTrainerBTrapped.envSettings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas = new ChargeEnvironmentLambdas(RunnerAgentChargeNeuralTrainerBTrapped.envSettings);
        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(RunnerAgentChargeNeuralTrainerBTrapped.envSettings);

        ChargeAgentFactory agentFactory=ChargeAgentFactory.builder()
                .environment(environment).envSettings(envSettings)
                .build();
        agent = agentFactory.buildAgent(ChargeState.newDummy(),NOF_LAYERS_HIDDEN,NOF_NEURONS_HIDDEN);
        TrainerFactory<ChargeVariables> trainerHelper= TrainerFactory.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .nofEpis(NOF_EPIS).nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.bTrappedAHasRandomSitePosAndRandomSoC())
                .build();
        trainer=trainerHelper.buildTrainer();
        trainer.train();
        doPlotting(envSettings);

        ChargeScenariosEvaluator evaluator = ChargeScenariosEvaluator.newSingleScenario(
                ChargeScenariosFactory.BtTrapped_AatPos0_bothMaxSoC_100steps,
                environment,agent);
        evaluator.evaluate();
        out.println("evaluator = " + evaluator);

    }

    private static void doPlotting(ChargeEnvironmentSettings envSettings) {
        ChargePlotHelper plotHelper=new ChargePlotHelper(agent,trainer);
        plotHelper.plotTdError();
        plotHelper.plotSumRewardsTracker();
        plotHelper.createScatterPlot(envSettings, "V-30", 0.3, plotHelper.TRAP_POS);
        plotHelper.createScatterPlot(envSettings, "V-80", 0.8, plotHelper.TRAP_POS);
        plotHelper.plotV20MinusV11VersusSoC(POS_B, SOC_B);
    }

}
