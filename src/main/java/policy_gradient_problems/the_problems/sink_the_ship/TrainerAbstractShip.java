package policy_gradient_problems.the_problems.sink_the_ship;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.abstract_classes.TrainerA;
import policy_gradient_problems.common.*;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.common_value_classes.ExperienceContAction;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;

@Log
public class TrainerAbstractShip extends TrainerA<VariablesShip> {

    public static final double DUMMY_VALUE = 0d;
    @NonNull EnvironmentShip environment;
    @NonNull AgentShip agent;

    public TrainerAbstractShip(@NonNull EnvironmentShip environment,
                               @NonNull AgentShip agent,
                               @NonNull TrainerParameters parameters) {
        this.environment = environment;
        this.agent = agent;
        super.parameters=parameters;
    }

    void updateTracker(int ei, TabularValueFunction valueFunction ) {
        for (int s: EnvironmentShip.POSITIONS) {
            Pair<Double, Double> msPair = agent.getMeanAndStdFromThetaVector(s);
            double valueState=valueFunction.getValue(s);
            var listForPlotting=List.of(msPair.getFirst(),msPair.getSecond(),valueState);
            tracker.addMeasures(ei,s,listForPlotting );
        }
    }

    public void setAgent(@NotNull AgentShip agent) {
        this.agent = agent;
    }

    @Override
    public void train() {

    }

    public List<Experience<VariablesShip>> getExperiences(AgentI<VariablesShip> agent) {
        List<Experience<VariablesShip>> experienceList=new ArrayList<>();
        int si = 0;
        StepReturn<VariablesShip> sr;
        do  {
            StateI<VariablesShip> state = agent.getState();
            Action action=agent.chooseAction();
            sr=environment.step(state,action);
            agent.setState(sr.state());
            var stateNew=sr.state();
            experienceList.add(new Experience<>(state, action, sr.reward(), stateNew, DUMMY_VALUE));
            si++;
        } while(isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    protected boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesShip> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }


}
