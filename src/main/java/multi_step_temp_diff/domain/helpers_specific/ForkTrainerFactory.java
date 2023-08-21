package multi_step_temp_diff.domain.helpers_specific;

import lombok.Builder;
import lombok.NonNull;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.ReplayBufferInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

@Builder
public class ForkTrainerFactory {

    AgentNeuralInterface<ForkVariables> agent;
    @NonNull ForkEnvironment environment;
    @NonNull Double probStart = 0.1;
    @NonNull Double probEnd = 1.0;
    @NonNull Integer  batchSize = 1;
    @NonNull Integer nofEpis = 3;
    @NonNull Integer maxTrainingTimeInSeconds = 10;
    @NonNull Integer nofStepsBetweenUpdatedAndBackuped = 10;

    public void setAgent(AgentNeuralInterface<ForkVariables> agent) {
        this.agent = agent;
    }

    public  NStepNeuralAgentTrainer<ForkVariables>  buildTrainer(ReplayBufferInterface<ForkVariables> buffer) {

        NStepNeuralAgentTrainerSettings settings = NStepNeuralAgentTrainerSettings.builder()
                .probStart(probStart).probEnd(probEnd).nofIterations(1)
                .batchSize(batchSize)
                .nofEpis(nofEpis)
                .maxTrainingTimeInMilliS(1000 * maxTrainingTimeInSeconds)
                .nofStepsBetweenUpdatedAndBackuped(nofStepsBetweenUpdatedAndBackuped)
                .build();

        return NStepNeuralAgentTrainer.<ForkVariables>builder()
                .settings(settings)
                .startStateSupplier(() -> ForkState.newFromRandomPos())
                .agentNeural(agent)
                .environment(environment)
                .buffer(buffer)
                .build();

    }

}
