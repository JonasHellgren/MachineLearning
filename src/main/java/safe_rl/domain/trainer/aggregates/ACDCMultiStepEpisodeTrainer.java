package safe_rl.domain.trainer.aggregates;

import lombok.NonNull;
import safe_rl.domain.agent.interfaces.AgentACDiscoI;
import safe_rl.domain.trainer.helpers.MultiStepActorUpdater;
import safe_rl.domain.trainer.helpers.MultiStepCriticUpdater;
import safe_rl.domain.trainer.helpers.MultiStepResultsGenerator;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import safe_rl.domain.trainer.value_objects.TrainerParameters;
import java.util.List;

public class ACDCMultiStepEpisodeTrainer<V> {

    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    MultiStepResultsGenerator<V> generator;
    MultiStepActorUpdater<V> actorUpdater;
    MultiStepCriticUpdater<V> criticUpdater;


    public ACDCMultiStepEpisodeTrainer(@NonNull AgentACDiscoI<V> agent,
                                       @NonNull TrainerParameters parameters) {
        this.agent = agent;
        this.parameters = parameters;
        generator=new MultiStepResultsGenerator<>(parameters,agent);
        actorUpdater=new MultiStepActorUpdater<>(agent,parameters);
        criticUpdater=new MultiStepCriticUpdater<>(agent,parameters);
    }

    public MultiStepResults<V> trainAgentFromExperiences(List<Experience<V>> experienceList,
                                          List<Double> lossCritic) {
        var multiStepResults=generator.generate(experienceList);
        actorUpdater.update(multiStepResults,lossCritic);
        criticUpdater.update(multiStepResults);
        return multiStepResults;
    }

}
