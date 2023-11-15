package policy_gradient_problems.short_corridor;

import common.MySetUtils;
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

@Builder
@Log
public class TrainerSC {

    public static final double DUMMY_VALUE = 0d;
    @NonNull EnvironmentSC environment;
    @NonNull AgentSC agent;
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
            agent.setStateAsRandomNonTerminal();
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,gamma);
            for (Experience experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(learningRate * vt);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            updateTracker(ei);
        }
    }

    private void updateTracker(int ei) {
        for (int s: EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            tracker.addActionProbabilities(ei,s, agent.calcActionProbabilitiesInState(s));
        }
    }

    private void logging(Experience experience, RealVector changeInThetaVector, double vt) {
        System.out.println("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }

    @NotNull
    private List<Experience> getExperiences() {
        List<Experience> experienceList=new ArrayList<>();
        int si = 0;
        StepReturnSC sr;
        do  {
            int observedStateOld = environment.getObservedState(agent.getState());
            int action=agent.chooseAction(observedStateOld);
            sr=environment.step(agent.getState(),action);
            agent.setState(sr.state());
            experienceList.add(new Experience(observedStateOld, action, sr.reward(), DUMMY_VALUE));
            si++;
        } while(!sr.isTerminal() && si < nofStepsMax);
        return experienceList;
    }


    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

}
