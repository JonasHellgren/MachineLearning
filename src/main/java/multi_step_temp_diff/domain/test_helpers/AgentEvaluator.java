package multi_step_temp_diff.domain.test_helpers;

import lombok.Builder;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;

import static java.lang.System.out;

@Builder
@Log
public class AgentEvaluator<S> {

    public static final int MAX_STEPS = 10_000;
    EnvironmentInterface<S> environment;
    AgentNeuralInterface<S> agent;
    @Builder.Default
    Integer simStepsMax = MAX_STEPS;

    public AgentEvaluatorResults simulate(StateInterface<S> initState) {
        final double probRandom = 0d;
        double sumRewards=0;
        int dummyAction = 0;
        StepReturn<S> stepReturn = environment.step(initState, dummyAction);
        int step;
        for (step = 0; step < simStepsMax; step++) {
            agent.setState(initState);
            int action = agent.chooseAction(probRandom);
            log.fine("state = " + initState);
            stepReturn = environment.step(initState, action);
            initState.setFromReturn(stepReturn);
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
