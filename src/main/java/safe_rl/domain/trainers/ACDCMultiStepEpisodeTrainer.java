package safe_rl.domain.trainers;

import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.*;
import safe_rl.helpers.*;
import java.util.List;
import java.util.Optional;

public class ACDCMultiStepEpisodeTrainer<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    MultiStepResultsGenerator<V> generator;
    MultiStepActorUpdater<V> actorUpdater;
    MultiStepCriticUpdater<V> criticUpdater;

    MultiStepResults<V> multiStepResults;

    public ACDCMultiStepEpisodeTrainer(@NonNull AgentACDiscoI<V> agent,
                                       @NonNull TrainerParameters parameters) {
        this.agent = agent;
        this.parameters = parameters;
        generator=new MultiStepResultsGenerator<>(parameters,agent);
        actorUpdater=new MultiStepActorUpdater<>(agent,parameters);
        criticUpdater=new MultiStepCriticUpdater<>(agent,parameters);
    }

    public Optional<MultiStepResults<V>> getMultiStepResultsFromPrevFit() {
        return multiStepResults==null?Optional.empty():Optional.of(multiStepResults);
    }

    //@Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList,
                                          List<Double> lossCritic) {
        multiStepResults=generator.generate(experienceList);
        actorUpdater.update(multiStepResults,lossCritic);
        criticUpdater.update(multiStepResults);
    }

}
