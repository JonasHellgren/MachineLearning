package multi_step_temp_diff.domain.helpers_common;

import lombok.Builder;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;


/**
 * Runs an agent from initState
 *
 */
@Builder
@Log
public class AgentEvaluator<S> {

    public static final int MAX_STEPS = 10_000;
    public static final double PROB_RANDOM = 0d;
    EnvironmentInterface<S> environment;
    AgentInterface<S> agent;
    @Builder.Default
    Integer simStepsMax = MAX_STEPS;

    public AgentEvaluatorResults simulate(final StateInterface<S> initState) {
        double sumRewards=0;
        int dummyAction = 0;
        StepReturn<S> stepReturn = environment.step(initState, dummyAction);
        int step;
        StateInterface<S> state=initState.copy();
        for (step = 0; step < simStepsMax; step++) {
            log.fine("stateNew = " + state);
            agent.setState(state);
            int action = agent.chooseAction(PROB_RANDOM);
            stepReturn = environment.step(state, action);
            state.setFromReturn(stepReturn);
            sumRewards+=stepReturn.reward;
            if (stepReturn.isNewStateFail) {
                break;
            }
        }

        return AgentEvaluatorResults.builder()
                .endedInFailState(stepReturn.isNewStateTerminal)
                .nofSteps(step)
                .sumRewards(sumRewards)
                .build();
    }

}
