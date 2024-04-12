package policy_gradient_problems.environments.sink_the_ship;

import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.AgentA;
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
    public static final int PROB_ACTION = 0;
    @NonNull EnvironmentShip environment;

    protected TrainerAbstractShip(@NonNull EnvironmentShip environment,
                                  @NonNull TrainerParameters parameters) {
        this.environment = environment;
        super.parameters = parameters;
    }

    void updateTracker( Map<Integer, List<Double>> map) {
        super.getRecorderStateValues().addStateValuesMap(map);
    }


    protected List<Experience<VariablesShip>> getExperiences(AgentI<VariablesShip> agent) {
        List<Experience<VariablesShip>> experienceList = new ArrayList<>();
        int si = 0;
        StepReturn<VariablesShip> sr;
        do {
            StateI<VariablesShip> state = agent.getState();
            Action action = agent.chooseAction();
            sr = environment.step(state, action);

            if (sr.isTerminal()) {
                log.fine("hitting, s="+state.getVariables().pos());
            }

            agent.setState(sr.state());
            var stateNew = sr.state();
            experienceList.add(Experience.ofWithIsTerminal(
                    state, action, sr.reward(), stateNew, PROB_ACTION,sr.isTerminal()));
            si++;
        } while (isNotTerminalAndNofStepsNotExceeded(si, sr));
        return experienceList;
    }

    protected boolean isNotTerminalAndNofStepsNotExceeded(int si, StepReturn<VariablesShip> sr) {
        return !sr.isTerminal() && si < parameters.nofStepsMax();
    }


}
