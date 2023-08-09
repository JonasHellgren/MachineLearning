package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.AgentEvaluator;
import multi_step_temp_diff.domain.helpers_common.AgentEvaluatorResults;
import multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargePlotHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargeStateSuppliers;
import multi_step_temp_diff.domain.helpers_specific.ChargeTrainerNeuralHelper;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper.*;

public class RunnerAgentChargeNeuralTrainerBothFree {

    private static final int NOF_EPIS = 200;
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    public static final int MAX_NOF_STEPS_TRAINING = 200;
    public static final int BATCH_SIZE1 = 100;
    public static final int SIM_STEPS_MAX_EVAL = 100;
    public static final int TIME_BUDGET_RESET = 1000;


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

        agent=buildAgent(ChargeState.newDummy());
        ChargeTrainerNeuralHelper<ChargeVariables> trainerHelper= ChargeTrainerNeuralHelper.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .batchSize(BATCH_SIZE1)
                .nofEpis(NOF_EPIS).nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndMaxSoC())
                .build();
        trainer=trainerHelper.buildTrainer();
        trainer.train();
        doPlotting(envSettings);
        evaluate(envSettings);

    }

    private static void doPlotting(ChargeEnvironmentSettings envSettings) {
        ChargePlotHelper plotHelper=new ChargePlotHelper(agent,trainer);
        plotHelper.plotTdError();
        plotHelper.plotSumRewardsTracker();
        int posB = 0;
        double socB = 1.0;
        plotHelper.createScatterPlot(envSettings, "V-30", 0.3, posB);
        plotHelper.createScatterPlot(envSettings, "V-80", 0.8, posB);
        plotHelper.plotV20MinusV11VersusSoC(posB, socB);
    }

    private static void evaluate(ChargeEnvironmentSettings envSettings) {
        environment = new ChargeEnvironment(envSettings);
        ChargeState initState = new ChargeState(ChargeVariables.builder().posA(0).posB(1).socA(0.99).build());
        AgentEvaluator<ChargeVariables> evaluator = AgentEvaluator.<ChargeVariables>builder()
                .environment(environment).agent(agent).simStepsMax(SIM_STEPS_MAX_EVAL)
                .build();
        AgentEvaluatorResults results = evaluator.simulate(initState);
        out.println("results = " + results);
    }

    private static  AgentNeuralInterface<ChargeVariables> buildAgent(ChargeState initState) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(0.5).discountFactor(0.95).momentum(0.1d)
                .nofNeuronsHidden(20).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(5)
                .valueNormalizer(new NormalizerMeanStd(List.of(
                        envSettingsForTraining.rewardBad() * 10, 0d, -1d, -2d, 0d, -1d, 0d)))
                .build();

        agent = AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseValue(
                                agentSettings,
                                envSettingsForTraining,
                                NORMALIZER_ONEDOTONE, VALUE_IF_NOT_OCCUPIED))
                .build();

        ChargeAgentNeuralHelper helper = ChargeAgentNeuralHelper.builder()
                .agent(agent).build();
        helper.resetAgentMemory(envSettingsForTraining, 1000, TIME_BUDGET_RESET);

        return agent;
    }


}