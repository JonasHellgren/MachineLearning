package multi_step_temp_diff.runners;

import multi_step_temp_diff.domain.helpers_common.AgentEvaluator;
import multi_step_temp_diff.domain.helpers_common.AgentEvaluatorResults;
import multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargePlotHelper;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.HotEncodingSoCAtOccupiedElseValue;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.agent_abstract.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.helpers_specific.ChargeStateSuppliers;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

import static java.lang.System.out;

@Log
public class RunnerAgentChargeNeuralTrainerBTrapped {

    private static final int NOF_EPIS = 30;

    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 100, MAX_BUFFER_SIZE_EXPERIENCE = 100_000;
    public static final double VALUE_IF_NOT_OCCUPIED = 1.1d;
    public static final NormalizerMeanStd NORMALIZER_ONEDOTONE =
            new NormalizerMeanStd(List.of(0.3, 0.5, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d));
    public static final int POS_B = 29; //trap
    public static final double SOC_B = 1.0;
    public static final int MAX_NOF_STEPS_TRAINING = 100;
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

        buildAgent(ChargeState.newDummy());
        buildTrainer(NOF_EPIS, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        trainer.train();
        doPlotting(envSettings);
        evaluate(envSettings);

    }

    private static void doPlotting(ChargeEnvironmentSettings envSettings) {
        ChargePlotHelper plotHelper=new ChargePlotHelper(agent,trainer);
        plotHelper.plotTdError();
        plotHelper.plotSumRewardsTracker();
        plotHelper.createScatterPlot(envSettings, "V-30", 0.3);
        plotHelper.createScatterPlot(envSettings, "V-80", 0.8);
        plotHelper.plotV20MinusV11VersusSoC(POS_B, SOC_B);
    }

    private static void evaluate(ChargeEnvironmentSettings envSettings) {
        environment = new ChargeEnvironment(envSettings);
        ChargeState initState = new ChargeState(ChargeVariables.builder().posA(0).posB(POS_B).socA(0.99).build());
        AgentEvaluator<ChargeVariables> evaluator = AgentEvaluator.<ChargeVariables>builder()
                .environment(environment).agent(agent).simStepsMax(100)
                .build();
        AgentEvaluatorResults results = evaluator.simulate(initState);
        out.println("results = " + results);
    }

    private static void buildAgent(ChargeState initState) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(0.1).discountFactor(0.99).momentum(0.1d)
                .nofNeuronsHidden(20).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(5)
                .valueNormalizer(new NormalizerMeanStd(List.of(envSettingsForTraining.rewardBad() * 10, 0d, -1d, -2d, 0d, -1d, 0d)))
                .build();

        agent = AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new HotEncodingSoCAtOccupiedElseValue(
                                agentSettings,
                                environmentCasted.getSettings(),
                                NORMALIZER_ONEDOTONE, VALUE_IF_NOT_OCCUPIED))
                .build();

        ChargeAgentNeuralHelper helper = ChargeAgentNeuralHelper.builder()
                .agent(agent).build();
        helper.resetAgentMemory(envSettingsForTraining, 1000, TIME_BUDGET_RESET);
    }

    public static void buildTrainer(int nofEpis, int nofSteps) {
        NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.builder()
                .probStart(0.5).probEnd(1e-3).nofIterations(1)
                .batchSize(BATCH_SIZE).maxBufferSize(MAX_BUFFER_SIZE_EXPERIENCE)
                .nofEpis(nofEpis)
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .build();

        ChargeStateSuppliers stateSupplier = new ChargeStateSuppliers(envSettingsForTraining);

        trainer = NStepNeuralAgentTrainer.<ChargeVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> stateSupplier.bTrappedAHasRandomSitePosAndRandomSoC())
                .agentNeural(agent)
                .environment(environment)
                .build();

    }


}
