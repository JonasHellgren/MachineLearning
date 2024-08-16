package safe_rl.domain.trainer.aggregates;

import safe_rl.domain.trainer.helpers.MultiStepActorUpdater;
import safe_rl.domain.trainer.helpers.MultiStepCriticUpdater;
import safe_rl.domain.trainer.helpers.MultiStepResultsGenerator;
import safe_rl.domain.trainer.mediators.MediatorMultiStepI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.value_objects.MultiStepResults;
import java.util.List;

public class ACDCMultiStepEpisodeFitter<V> {
    MediatorMultiStepI<V> mediator;

    MultiStepResultsGenerator<V> generator;
    MultiStepActorUpdater<V> actorUpdater;
    MultiStepCriticUpdater<V> criticUpdater;

    public ACDCMultiStepEpisodeFitter(MediatorMultiStepI<V> mediator) {
        this.mediator=mediator;
        var agent=mediator.getExternal().agent();
        var parameters=mediator.getParameters();
        generator=new MultiStepResultsGenerator<>(parameters,agent);
        actorUpdater=new MultiStepActorUpdater<>(agent,parameters);
        criticUpdater=new MultiStepCriticUpdater<>(agent,parameters);
    }


    public MultiStepResults<V> trainAgentFromExperiences(List<Experience<V>> experienceList,
                                          List<Double> lossCritic) {
        var multiStepResults=generator.generate(experienceList);
        actorUpdater.update(multiStepResults);
        criticUpdater.update(multiStepResults);
        mediator.maybePenalizeActionCorrection(multiStepResults,lossCritic);
        return multiStepResults;
    }

}
