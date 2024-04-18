package safe_rl.domain.episode_trainers;

import lombok.Builder;
import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.*;
import safe_rl.helpers.ReturnCalculator;
import java.util.List;
import java.util.function.Function;

@Builder
public class ACDCOneStepEpisodeTrainer<V> {

    public static final double VALUE_TERM = 0d;
    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Function<V, Integer> tabularCoder;  //transforms state to key used by critic function

    //@Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            agent.setState(experience.state());
            double tdError = calcTdError(experience);
            agent.fitActor(experience.actionApplied(),tdError);
            agent.fitCritic(tdError);
        }
    }

    private double calcTdError(Experience<V> experience) {
        double v = agent.readCritic();
        double vNext = experience.isTerminalApplied()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNextApplied());
        return experience.rewardApplied() + parameters.gamma() * vNext - v;
    }

}
