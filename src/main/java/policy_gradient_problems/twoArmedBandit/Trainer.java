package policy_gradient_problems.twoArmedBandit;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.helpers.ReturnCalculator;

import java.util.ArrayList;
import java.util.List;


/***
 * agent.theta <- agent.theta+learningRate*gradLog*vt;
 *
 */
@Builder
@Log
public class Trainer {

    public static final double DUMMY_VALUE = 0d;
    @NonNull Environment environment;
    @NonNull Agent agent;
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
            for (Experience experience:experienceListWithReturns) {
                var gradLogVector = agent.gradLogVector(experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(learningRate * vt);
                logging(experience, changeInThetaVector,vt);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            tracker.addActionProbabilities(0,agent.actionProbabilities());
        }
    }

    private void logging(Experience experience, RealVector changeInThetaVector, double vt) {
        log.fine("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }

    @NotNull
    private List<Experience> getExperiences() {
        List<Experience> experienceList=new ArrayList<>();
        for (int si = 0; si < nofStepsMax ; si++) {
            int action=agent.chooseAction();
            double reward=environment.step(action);
            experienceList.add(new Experience(action,reward, DUMMY_VALUE));
        }
        return experienceList;
    }


    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

}
