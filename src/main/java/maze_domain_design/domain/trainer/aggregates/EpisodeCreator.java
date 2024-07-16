package maze_domain_design.domain.trainer.aggregates;

import lombok.Setter;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.trainer.entities.Experience;

public class EpisodeCreator {
    @Setter MediatorI mediator;

    public Episode runEpisode() {
        var agent = mediator.getExternal().agent();
        var env = mediator.getExternal().environment();
        var props = mediator.getProperties();

        var episode = new Episode();
        var s = mediator.getStartState();

        int t = 0;
        while (!s.isTerminal()) {
            Action a = agent.chooseAction(s, 1);
            var sr = env.step(s, a);
            var e = Experience.ofIdStateActionStepReturn(t, s, a, sr);
            episode.addExp(e);
            s = sr.sNext();
        }

        return episode;

    }


}
