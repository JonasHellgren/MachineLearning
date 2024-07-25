package domain_design_tabular_q_learning.domain.trainer.aggregates;

import common.list_arrays.ListUtils;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.environments.obstacle_on_road.ActionRoad;
import domain_design_tabular_q_learning.domain.trainer.entities.Experience;
import domain_design_tabular_q_learning.domain.trainer.value_objects.EpisodeInfoForRecording;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class EpisodeCreator<V,A> {
    MediatorI<V,A> mediator;

    public Episode<V,A> runEpisode() {
        var agent = mediator.getExternal().agent();
        var env = mediator.getExternal().environment();
        var episode = new Episode<V,A>();
        var s = env.getStartState();
        var pRandomAction = mediator.pRandomAction();
        List<Double> tdErrors = new ArrayList<>();
        while (!s.isTerminal()) {
            ActionI<A> a = agent.chooseAction(s, pRandomAction);
            var sr = env.step(s, a);
            int t = episode.nextId();
            var e = Experience.ofIdStateActionStepReturn(t, s, a, sr);
            episode.addExp(e);
            double err = mediator.fitAgentMemoryFromExperience(e);
            tdErrors.add(Math.abs(err));
            s = sr.sNext();
        }

        setEpisodeInfo(episode, pRandomAction, ListUtils.findAverage(tdErrors).orElseThrow());
        return episode;
    }

    private static <V,A> void setEpisodeInfo(Episode<V,A> episode,
                                             double pRandomAction,
                                             double tdErrorAvg) {
        episode.infoForRecording = EpisodeInfoForRecording.builder()
                .pRandomAction(pRandomAction).tdErrorAvg(tdErrorAvg)
                .build();
    }


}
