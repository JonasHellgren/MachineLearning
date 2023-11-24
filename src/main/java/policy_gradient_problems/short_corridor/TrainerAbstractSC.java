package policy_gradient_problems.short_corridor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TrainerParameters;
import policy_gradient_problems.common.TrainingTracker;

import java.util.ArrayList;
import java.util.List;

@Log
@AllArgsConstructor
public class TrainerAbstractSC {

    public static final double DUMMY_VALUE = 0d;
    @NonNull EnvironmentSC environment;
    @NonNull AgentSC agent;
    @NonNull TrainerParameters parameters;
    @NonNull TrainingTracker tracker;

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return tracker;
    }

    void updateTracker(int ei) {
        for (int s: EnvironmentSC.SET_OBSERVABLE_STATES_NON_TERMINAL) {
            tracker.addActionProbabilities(ei,s, agent.calcActionProbabilitiesInState(s));
        }
    }

    public void setAgent(AgentSC agent) {
        this.agent = agent;
    }

    @NotNull
    List<Experience> getExperiences() {
        List<Experience> experienceList=new ArrayList<>();
        int si = 0;
        StepReturnSC sr;
        do  {
            int observedStateOld = environment.getObservedState(agent.getState());
            int action=agent.chooseAction(observedStateOld);
            sr=environment.step(agent.getState(),action);
            agent.setState(sr.state());
            int observerdStateNew=environment.getObservedState(sr.state());
            experienceList.add(new Experience(observedStateOld, action, sr.reward(), observerdStateNew, DUMMY_VALUE));
            si++;
        } while(!sr.isTerminal() && si < parameters.nofEpisodes());
        return experienceList;
    }


    private void logIfEmptyTracker() {
        if (tracker.isEmpty()) {
            log.warning("Need to train first");
        }
    }

    private void logging(Experience experience, RealVector changeInThetaVector, double vt) {
        System.out.println("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }


}
