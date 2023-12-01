package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.common.ReturnCalculator;

import java.util.ArrayList;
import java.util.List;


/***
 * agent.theta <- agent.theta+learningRate*gradLog*vt;
 *
 */
@Builder
@Log
public class TrainerBandit {

    public static final double DUMMY_VALUE = 0d;
    public static final int STATE_DUMMY = 0;
    @NonNull EnvironmentBandit environment;
    @NonNull AgentBandit agent;
    @NonNull Integer nofEpisodes;
    @NonNull Integer nofStepsMax;
    @NonNull Double gamma, learningRate;

    @Builder.Default
    final TrainingTracker tracker=new TrainingTracker();

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return tracker;
    }

    public void train() {
        var returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < nofEpisodes; ei++) {
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,gamma);
            for (ExperienceDiscreteAction experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(learningRate * vt);
                logging(experience, changeInThetaVector,vt);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            tracker.addMeasures(ei,0,agent.actionProbabilities());
        }
    }

    private void logging(ExperienceDiscreteAction experience, RealVector changeInThetaVector, double vt) {
        log.fine("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }

    @NotNull
    private List<ExperienceDiscreteAction> getExperiences() {
        List<ExperienceDiscreteAction> experienceList=new ArrayList<>();
        for (int si = 0; si < nofStepsMax ; si++) {
            int action=agent.chooseAction();
            double reward=environment.step(action);
            experienceList.add(new ExperienceDiscreteAction(STATE_DUMMY,action,reward, STATE_DUMMY, DUMMY_VALUE));
        }
        return experienceList;
    }


    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

}
