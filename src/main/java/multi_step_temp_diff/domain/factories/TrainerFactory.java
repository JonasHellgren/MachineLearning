package multi_step_temp_diff.domain.factories;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Supplier;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.*;


@Builder
public class TrainerFactory<S> {

    public static final int NOF_EPIS_BETWEEN_LOGS = 100;
    @NonNull AgentNeuralInterface<S> agent;
    @NonNull EnvironmentInterface<S> environment;

    @Builder.Default
    int nofEpis=NOF_EPIS;
    @Builder.Default
    int nofStepsBetweenUpdatedAndBackuped=NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED;
    @Builder.Default
    int batchSize=BATCH_SIZE;
    @Builder.Default
    int maxBufferSize= MAX_BUFFER_SIZE_EXPERIENCE_REPLAY;
    @Builder.Default
    Pair<Double,Double> startEndProb= START_END_PROB;
    @NonNull Supplier<StateInterface<S>> startStateSupplier;
    @Builder.Default
    int maxTrainingTimeInMilliS=Integer.MAX_VALUE;
    @Builder.Default
    int nofEpisodesBetweenLogs= NOF_EPIS_BETWEEN_LOGS;
    @NonNull
    ReplayBufferInterface<S> buffer;

    public NStepNeuralAgentTrainer<S> buildTrainer() {
        NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.builder()
                .probStart(startEndProb.getLeft()).probEnd(startEndProb.getRight()).nofIterations(1)
                .batchSize(batchSize).maxBufferSize(MAX_BUFFER_SIZE_EXPERIENCE_REPLAY)
                .nofEpis(nofEpis).maxTrainingTimeInMilliS(maxTrainingTimeInMilliS)
                .nofStepsBetweenUpdatedAndBackuped(nofStepsBetweenUpdatedAndBackuped)
                .nofEpisodesBetweenLogs(nofEpisodesBetweenLogs)
                .build();

        return NStepNeuralAgentTrainer.<S>builder()
                .settings(settings)
                .startStateSupplier(startStateSupplier)
                .agentNeural(agent)
                .environment(environment)
                .buffer(buffer)
                .build();
    }

}
