package safe_rl.domain.trainer.aggregates;

import lombok.AllArgsConstructor;
import safe_rl.domain.trainer.mediators.MediatorSingleStepI;
import safe_rl.domain.trainer.value_objects.Experience;
import safe_rl.domain.trainer.helpers.CorrectedActionPenalizer;
import safe_rl.domain.trainer.helpers.ReturnCalculator;

import java.util.List;

//@Builder
@AllArgsConstructor
public class ACDCOneStepEpisodeFitter<V> {

    MediatorSingleStepI<V> mediator;

    public static final double VALUE_TERM = 0d;
    public static final int LENGTH_WINDOW = 100;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList, List<Double> lossCriticList) {
        var rc = new ReturnCalculator<V>();
        var parameters=mediator.getParameters();
        var agent=mediator.getExternal().agent();
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
        var parameters=mediator.getParameters();
        var agent=mediator.getExternal().agent();
        double v = agent.readCritic(experience.state());
        double vNext = experience.isTerminalApplied()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNextApplied());
        return experience.rewardApplied() + parameters.gamma() * vNext - v;
    }

}


