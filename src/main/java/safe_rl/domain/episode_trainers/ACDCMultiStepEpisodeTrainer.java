package safe_rl.domain.episode_trainers;

import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.*;
import safe_rl.helpers.*;
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

    //@Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList,
                                          List<Double> lossCritic) {
        var msr=generator.generate(experienceList);
        actorUpdater.update(msr,lossCritic);
        criticUpdater.update(msr);
    }

}
