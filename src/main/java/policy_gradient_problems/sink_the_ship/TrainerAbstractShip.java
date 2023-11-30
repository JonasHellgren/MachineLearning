package policy_gradient_problems.sink_the_ship;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.common.*;

import java.util.ArrayList;
import java.util.List;

@Log
@AllArgsConstructor
public class TrainerAbstractShip {

    public static final double DUMMY_VALUE = 0d;
    @NonNull EnvironmentShip environment;
    @NonNull AgentShip agent;
    @NonNull TrainerParameters parameters;
    @NonNull TrainingTracker tracker;

    public TrainingTracker getTracker() {
        logIfEmptyTracker();
        return tracker;
    }

    void updateTracker(int ei, TabularValueFunction valueFunction ) {
        for (int s: EnvironmentShip.STATES) {
            Pair<Double, Double> msPair = agent.getMeanAndStdFromThetaVector(s);
            double valueState=valueFunction.getValue(s);
            var listForPlotting=List.of(msPair.getFirst(),msPair.getSecond(),valueState);
            tracker.addMeasures(ei,s,listForPlotting );
        }
    }

    public void setAgent(AgentShip agent) {
        this.agent = agent;
    }

    List<ExperienceContAction> getExperiences() {
        List<ExperienceContAction> experienceList=new ArrayList<>();
        int si = 0;
        StepReturnShip sr;
        do  {
            int state = agent.getState();
            double action=agent.chooseAction(state);
            sr=environment.step(state,action);
            agent.setState(sr.state());
            int stateNew=sr.state();
            experienceList.add(new ExperienceContAction(state, action, sr.reward(), stateNew, DUMMY_VALUE));
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    //todo to TrainerAbstract
    private boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturnShip sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
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
