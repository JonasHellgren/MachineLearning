package safe_rl.domain.episode_trainers;

import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.MultiStepActorUpdater;
import safe_rl.helpers.MultiStepCriticUpdater;
import safe_rl.helpers.MultiStepResultsGenerator;
import safe_rl.helpers.ReturnCalculator;

import java.util.List;
public class ACDCMultiStepEpisodeTrainer<V> {

    public static final double VALUE_TERM = 0d;
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

    public void trainAgentFromExperiencesOld(List<Experience<V>> experienceList,
                                             List<Double> lossCritic) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        double avgCriticLoss= ListUtils.findAverage(lossCritic).orElse(0)*parameters.ratioPenCorrectedAction();
        for (Experience<V> e : elwr) {
            //agent.setState(e.state());
            double tdError = calcTdError(e);
            penalizeAgentProposedActionIfSafeCorrected(avgCriticLoss, e);
            agent.fitActor(e.state(),e.actionApplied(), tdError);
            agent.fitCritic(e.state(),tdError);
        }
    }

    private  void penalizeAgentProposedActionIfSafeCorrected(double avgCriticLoss,
                                                             Experience<V> e) {
        Conditionals.executeIfTrue(e.isSafeCorrected(),
                () -> agent.fitActor(e.state(),e.ars().action(), -avgCriticLoss));
    }


    private double calcTdError(Experience<V> experience) {
        double v = agent.readCritic(experience.state());
        double vNext = experience.isTerminalApplied()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNextApplied());
        return experience.rewardApplied() + parameters.gamma() * vNext - v;
    }


}
