package multi_step_temp_diff.runners;

import common.ListUtils;
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
import multi_step_temp_diff.domain.helpers_specific.*;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;
import static multi_step_temp_diff.domain.helpers_specific.ChargeScenariosFactory.*;

/***
 * stor discountFactor gör mer instabilt
 */

public class RunnerAgentChargeNeuralTrainerBothFree {

    private static final int NOF_EPIS = 5000;
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    public static final int MAX_NOF_STEPS_TRAINING = 100;
    public static final int BATCH_SIZE1 = 30;
    public static final int TIME_BUDGET_RESET = 0;  //small <=> no reset
    public static final Pair<Double, Double> START_END_PROB = Pair.of(0.5, 1e-5);

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

        agent = buildAgent(ChargeState.newDummy());
        ChargeTrainerNeuralHelper<ChargeVariables> trainerHelper = ChargeTrainerNeuralHelper.<ChargeVariables>builder()
                .agent(agent).environment(environment)
                .batchSize(BATCH_SIZE1).startEndProb(START_END_PROB)
                .nofEpis(NOF_EPIS).nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                .build();
        trainer = trainerHelper.buildTrainer();
        trainer.train();
        doPlotting(envSettings);
        agent.saveMemory(FOLDER_NETWORKS + FILENAME_CHARGE_BOTH_FREE_NET);

        ChargeScenariosEvaluator evaluator = createChargeScenariosEvaluator();
        evaluator.evaluate();
        out.println("evaluator = " + evaluator);

    }

    @NotNull
    private static ChargeScenariosEvaluator createChargeScenariosEvaluator() {
        return ChargeScenariosEvaluator.builder()
                .environment(environment).agent(agent)
                .scenarios(List.of(
                        BatPos0_At1_BothHighSoC,
                        BatPos0_AtPosSplitCriticalSoCA,
                        BatPos0_At20_BothMaxSoC,
                        BatPosSplit_AatPos40_BothModerateSoC,
                        B3BehindModerateSoC_AatSplitModerateSoC,
                        B1BehindCriticalSoC_AatSplitModerateSoC,
                        B3BehindCriticalSoC_AatSplitModerateSoC,
                        BatPos0_At1_BothHighSoC_1000steps
                ))
                .build();
    }

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

    private static AgentNeuralInterface<ChargeVariables> buildAgent(ChargeState initState) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(0.01).discountFactor(0.99).momentum(0.01d)
                .nofLayersHidden(10).nofNeuronsHidden((int) Math.round(envSettingsForTraining.siteNodes().size() / 2d))
                .transferFunctionType(TransferFunctionType.TANH)
                .valueNormalizer(new NormalizerMeanStd(ListUtils.merge(
                        List.of(envSettingsForTraining.rewardBad() * ALPHA),
                        CHARGE_REWARD_VALUES_EXCEPT_FAIL)))
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
