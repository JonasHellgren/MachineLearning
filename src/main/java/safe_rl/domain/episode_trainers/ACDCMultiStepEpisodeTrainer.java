package safe_rl.domain.episode_trainers;

import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.helpers.MultiStepResultsGenerator;
import safe_rl.helpers.ReturnCalculator;

import java.util.List;
@Builder
public class ACDCMultiStepEpisodeTrainer<V> {

    public static final double VALUE_TERM = 0d;
    public static final int LENGTH_WINDOW = 100;
    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    //@Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList, List<Double> lossCritic) {
//        double avgCriticLoss= ListUtils.findAverage(lossCritic).orElse(0)*parameters.ratioPenCorrectedAction();
        var msResGen=new MultiStepResultsGenerator<V>(parameters,agent,true);
        var msActorUpdater=new MultiStepActorUpdater(agent,parameters,lossCritic);
        var msCriticUpdater=new MultiStepCriticUpdater(agent,parameters);

        var msr=msResGen.generate(experienceList);
        msActorUpdater.update(msr);
        msCriticUpdater.update(msr);
    }

    public void trainAgentFromExperiencesOld(List<Experience<V>> experienceList, List<Double> lossCritic) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        double avgCriticLoss= ListUtils.findAverage(lossCritic).orElse(0)*parameters.ratioPenCorrectedAction();
        for (Experience<V> e : elwr) {
            agent.setState(e.state());
            double tdError = calcTdError(e);
            penalizeAgentProposedActionIfSafeCorrected(avgCriticLoss, e);
            agent.fitActor(e.actionApplied(), tdError);
            agent.fitCritic(tdError);
        }
    }

    private  void penalizeAgentProposedActionIfSafeCorrected(double avgCriticLoss, Experience<V> e) {
        Conditionals.executeIfTrue(e.isSafeCorrected(),
                () -> agent.fitActor(e.ars().action(), -avgCriticLoss));
    }


    private double calcTdError(Experience<V> experience) {
        double v = agent.readCritic();
        double vNext = experience.isTerminalApplied()
                ? VALUE_TERM
                : agent.readCritic(experience.stateNextApplied());
        return experience.rewardApplied() + parameters.gamma() * vNext - v;
    }


}
