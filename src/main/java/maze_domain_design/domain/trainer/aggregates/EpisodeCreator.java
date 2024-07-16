package maze_domain_design.domain.trainer.aggregates;

import lombok.AllArgsConstructor;
import lombok.Setter;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.trainer.entities.Experience;

@AllArgsConstructor
public class EpisodeCreator {
    MediatorI mediator;

    public Episode runEpisode() {
        var agent = mediator.getExternal().agent();
        var env = mediator.getExternal().environment();
        var props = mediator.getProperties();

        var episode = new Episode();
        var s = mediator.getStartState();
        double trainingProgress= mediator.getRecorder().size()/ (double) props.nEpisodes();
        double pRandomAction=props.probRandomAction(trainingProgress);
        while (!s.isTerminal()) {
            Action a = agent.chooseAction(s, pRandomAction);
            var sr = env.step(s, a);
            int t=episode.nextId();
            var e = Experience.ofIdStateActionStepReturn(t, s, a, sr);
            episode.addExp(e);
            mediator.fitAgentMemoryFromExperience(e);
            s = sr.sNext();
        }

        return episode;

    }


}
