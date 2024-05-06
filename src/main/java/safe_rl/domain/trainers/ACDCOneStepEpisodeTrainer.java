package safe_rl.domain.trainers;

import common.list_arrays.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.*;
import safe_rl.helpers.CorrectedActionPenalizer;
import safe_rl.helpers.ReturnCalculator;

import java.util.List;

@Builder
public class ACDCOneStepEpisodeTrainer<V> {

    public static final double VALUE_TERM = 0d;
    public static final int LENGTH_WINDOW = 100;
    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList, List<Double> lossCriticList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        var penalizer = CorrectedActionPenalizer.<V>builder()
                .agent(agent).parameters(parameters).lossCriticList(lossCriticList)
                .build();

        for (Experience<V> e : elwr) {
            double tdError = calcTdError(e);
            penalizer.maybePenalize(e);
            agent.fitActor(e.state(),e.actionApplied(), tdError);
            agent.fitCritic(e.state(),tdError);
        }
    }

    private double calcTdError(Experience<V> experience) {
        double v = agent.readCritic(experience.state());
        double vNext = experience.isTerminalApplied()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNextApplied());
        return experience.rewardApplied() + parameters.gamma() * vNext - v;
    }

}


