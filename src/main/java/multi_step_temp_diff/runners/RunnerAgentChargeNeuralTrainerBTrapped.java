package multi_step_temp_diff.runners;

import common.MultiplePanelsPlotter;
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
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.normalizer.NormalizerMeanStd;
import multi_step_temp_diff.domain.test_helpers.ChargeStateSuppliers;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class RunnerAgentChargeNeuralTrainerBTrapped {
    private static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    private static final int BATCH_SIZE = 50, MAX_BUFFER_SIZE_EXPERIENCE = 100_000;

    private static final int NOF_EPIS = 20;
    public static final double DELTA = 5;
    public static final double VALUE_IF_NOT_OCCUPIED = 1.1d;
    public static final NormalizerMeanStd NORMALIZER_ONEDOTONE =
            new NormalizerMeanStd(List.of(0.3, 0.5, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d));
    public static final int LENGTH_FILTER_WINDOW = 100;

    static AgentNeuralInterface<ChargeVariables> agent;
    static NStepNeuralAgentTrainer<ChargeVariables> trainer;
    static EnvironmentInterface<ChargeVariables> environment;
    static ChargeEnvironmentLambdas lambdas;
    static ChargeEnvironment environmentCasted;
    static ChargeEnvironmentSettings envSettings;
    static ChargeState initState;
    static AgentChargeNeuralSettings agentSettings;

    public static void main(String[] args) {
        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(envSettings);
        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        buildAgent(initState);
        buildTrainer(NOF_EPIS, NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED);
        log.info("Training starting");
        trainer.train();
        log.info("Training finished");

        AgentInfo<ChargeVariables> agentInfo=new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories=new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_FILTER_WINDOW);
        listOfTrajectories.add(filtered1);
        MultiplePanelsPlotter plotter=new MultiplePanelsPlotter(Collections.singletonList("Error"), "Step");
        plotter.plot(listOfTrajectories);

    }

    private static void buildAgent(ChargeState initState) {
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.builder()
                .learningRate(0.1).discountFactor(0.9).momentum(0.1d)
                .nofNeuronsHidden(20).transferFunctionType(TransferFunctionType.GAUSSIAN)
                .nofLayersHidden(5)
                .valueNormalizer(new NormalizerMeanStd(List.of(envSettings.rewardBad()*10,0d,-1d,-2d,0d,-1d,0d)))
                //.valueNormalizer(new NormalizeMinMax(settings.rewardBad(),0))
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
    }

    public static void buildTrainer(int nofEpis,  int nofSteps) {
       // agentCasted=(AgentChargeNeural) agent;
        NStepNeuralAgentTrainerSettings settings= NStepNeuralAgentTrainerSettings.builder()
                .probStart(0.25).probEnd(1e-3).nofIterations(1)
                .batchSize(BATCH_SIZE).maxBufferSize(MAX_BUFFER_SIZE_EXPERIENCE)
                .nofEpis(nofEpis)
                .nofStepsBetweenUpdatedAndBackuped(nofSteps)
                .build();

        ChargeStateSuppliers stateSupplier=new ChargeStateSuppliers(envSettings);

        trainer= NStepNeuralAgentTrainer.<ChargeVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> stateSupplier.bTrappedAHasRandomSitePosAndRandomSoC())
                .agentNeural(agent)
                .environment(environment)
                .build();

    }


}
