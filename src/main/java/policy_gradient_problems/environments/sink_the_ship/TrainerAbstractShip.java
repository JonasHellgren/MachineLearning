package policy_gradient_problems.environments.sink_the_ship;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.agent_interfaces.AgentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.abstract_classes.TrainerA;
import policy_gradient_problems.domain.param_memories.CriticMemoryParamOneHot;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.StepReturn;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log
public abstract class TrainerAbstractShip extends TrainerA<VariablesShip> {
    @NonNull EnvironmentShip environment;
    @NonNull AgentShipParam agent;

    protected TrainerAbstractShip(@NonNull EnvironmentShip environment,
                                  @NonNull AgentShipParam agent,
                                  @NonNull TrainerParameters parameters) {
        this.environment = environment;
        this.agent = agent;
        super.parameters = parameters;
    }

    void updateTracker(CriticMemoryParamOneHot valueFunction) {
        Map<Integer, List<Double>> map = EnvironmentShip.POSITIONS.stream()
                .collect(Collectors.toMap(
                        s -> s,
                        s -> {
                            var msPair = agent.getMeanAndStdFromThetaVector(s);
                            double valueState = valueFunction.getValue(s);
                            return List.of(msPair.getFirst(), msPair.getSecond(), valueState);
                        }
                ));
        super.getRecorderStateValues().addStateValuesMap(map);
    }

    public void setAgent(@NotNull AgentShipParam agent) {
        this.agent = agent;
    }

    protected List<Experience<VariablesShip>> getExperiences(AgentI<VariablesShip> agent) {
        List<Experience<VariablesShip>> experienceList = new ArrayList<>();
        int si = 0;
        StepReturn<VariablesShip> sr;
        do {
            StateI<VariablesShip> state = agent.getState();
            Action action = agent.chooseAction();
            sr = environment.step(state, action);
            agent.setState(sr.state());
            var stateNew = sr.state();
            experienceList.add(Experience.of(state, action, sr.reward(), stateNew));
            si++;
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    protected boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesShip> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }


}
