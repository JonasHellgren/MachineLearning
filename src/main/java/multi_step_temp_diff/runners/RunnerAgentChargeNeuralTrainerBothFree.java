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
import multi_step_temp_diff.domain.helpers_common.AgentEvaluator;
import multi_step_temp_diff.domain.helpers_common.AgentEvaluatorResults;
import multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargePlotHelper;
import multi_step_temp_diff.domain.helpers_specific.ChargeStateSuppliers;
import multi_step_temp_diff.domain.helpers_specific.ChargeTrainerNeuralHelper;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.apache.commons.lang3.tuple.Pair;
import org.neuroph.util.TransferFunctionType;

import java.util.List;

import static java.lang.System.out;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentNeuralHelper.*;

/***
 * stor discountFactor gör mer instabilt
 */

public class RunnerAgentChargeNeuralTrainerBothFree {

    private static final int NOF_EPIS = 200;
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 10;
    public static final int MAX_NOF_STEPS_TRAINING = 1000;
    public static final int BATCH_SIZE1 = 100;
    public static final int SIM_STEPS_MAX_EVAL = 100;
    public static final int TIME_BUDGET_RESET = 0;  //small <=> no reset
    public static final Pair<Double, Double> START_END_PROB = Pair.of(0.5, 1e-5);
    public static final double ALPHA = 3d;


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
                .batchSize(BATCH_SIZE1).startEndProb(START_END_PROB)
                .nofEpis(NOF_EPIS).nofStepsBetweenUpdatedAndBackuped(NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED)
                .startStateSupplier(() -> stateSupplier.randomDifferentSitePositionsAndHighSoC())
                .build();
        trainer=trainerHelper.buildTrainer();
        trainer.train();
        doPlotting(envSettings);
        evaluate(envSettings);

        agent.saveMemory(FOLDER_NETWORKS +"chargeBothFreeNet.nnet");

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
                .learningRate(0.1).discountFactor(0.99).momentum(0.01d)
                .nofLayersHidden(10).nofNeuronsHidden((int) Math.round(envSettingsForTraining.siteNodes().size()/2d))
                .transferFunctionType(TransferFunctionType.TANH)
                .valueNormalizer(new NormalizerMeanStd(ListUtils.merge(
                        List.of(envSettingsForTraining.rewardBad() * ALPHA),
                        ChargeAgentNeuralHelper.CHARGE_REWARD_VALUES_EXCEPT_FAIL)))
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
