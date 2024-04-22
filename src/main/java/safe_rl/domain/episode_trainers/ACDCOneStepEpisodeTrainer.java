package safe_rl.domain.episode_trainers;

import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.value_classes.*;
import safe_rl.helpers.ReturnCalculator;
import java.text.DecimalFormat;
import java.util.List;

@Builder
public class ACDCOneStepEpisodeTrainer<V> {

    public static final double VALUE_TERM = 0d;
    public static final int LENGTH_WINDOW = 100;
    @NonNull AgentACDiscoI<V> agent;
    @NonNull TrainerParameters parameters;

    //@Override
    public void trainAgentFromExperiences(List<Experience<V>> experienceList, List<Double> lossCritic) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        double avgCriticLoss=ListUtils.findAverage(lossCritic).orElse(0)*parameters.ratioPenCorrectedAction();
        for (Experience<V> e : elwr) {
            agent.setState(e.state());
            double tdError = calcTdError(e);
            penalizeAgentProposedActionIfSafeCorrected(avgCriticLoss, e);
            agent.fitActor(e.actionApplied(), tdError);
            agent.fitCritic(tdError);
        }
    }

    //todo ändra logik ev egen klass
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

    private void print(DecimalFormat f, Experience<V> e, double tdError, Pair<Double, Double> gradLog, Pair<Double, Double> ms) {
        double vNext = e.isTerminalApplied()
                ? VALUE_TERM
                : agent.readCritic(e.stateNextApplied());
        System.out.println("e.state() = " + e.state() + "e.stateNextApplied() = " + e.stateNextApplied());
        System.out.println("ms = " + ms);

        System.out.println("a=" + e.actionApplied() + ", tdError = " + f.format(tdError) +
                ",r=" + f.format(e.rewardApplied()) + ",g*vNext=" + f.format(parameters.gamma() * vNext) + ",v=" + f.format(agent.readCritic()) +

                ", gradLog = " + gradLog);
    }

}


/*

            System.out.println("state="+experience.state()+"a="+experience.actionApplied()+
                    ", tdError = " + tdError+",v="+agent.readCritic()+", gradLog = " + gradLog+
                    "ms = " + ms);
 */
