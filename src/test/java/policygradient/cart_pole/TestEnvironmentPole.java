package policygradient.cart_pole;

import common.Counter;
import common.RandUtils;
import org.junit.jupiter.api.*;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.StepReturn;
import policy_gradient_problems.the_problems.cart_pole.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEnvironmentPole {

    private static final int NOT_TRIALS = 1000;

    EnvironmentPole environment;
    StepReturn<VariablesPole> stepReturn;

    @BeforeEach
    public void init() {
        environment = new EnvironmentPole(ParametersPole.newDefault());
    }

    @Test
    public void whenForceLeft_thenMovesCartLeftAndRotatesRight() {
        var state0 = applyActionUntilTermination(EnvironmentPole.ACTION_LEFT, StatePole.newUprightAndStill());
        var state=(StatePole) state0;
        assertTrue(state.x() < 0);
        assertTrue(state.angle() > 0);
        assertTrue(state.nofSteps() > 0);
    }

    @Test
    public void whenForceRight_thenMovesCartRightAndRotatesLeft() {
        var state0 = applyActionUntilTermination(EnvironmentPole.ACTION_RIGHT, StatePole.newUprightAndStill());
        var state=(StatePole) state0;
        assertTrue(state.x() > 0);
        assertTrue(state.angle() < 0);
        assertTrue(state.nofSteps() > 0);
    }

    @Test
    public void moreStepsFromUpRightComparedToRandom() {
        double averageNofStepsUpRight = calAverageNofStepsForRandomActions(StatePole.newUprightAndStill());
        double averageNofStepsRandom = calAverageNofStepsForRandomActions(
                StatePole.newAngleAndPosRandom(environment.getParameters()));

        System.out.println("averageNofStepsUpRight = " + averageNofStepsUpRight);
        System.out.println("averageNofStepsRandom = " + averageNofStepsRandom);
        assertTrue(averageNofStepsRandom < averageNofStepsUpRight);
    }

    private double calAverageNofStepsForRandomActions(StatePole stateStart) {
        List<Integer> nofStepsUpRightList = new ArrayList<>();
        for (int i = 0; i < NOT_TRIALS; i++) {
            int nofSteps = applyRandomActionUntilTermination(stateStart.copy());
            nofStepsUpRightList.add(nofSteps);
        }
        return nofStepsUpRightList.stream().mapToInt(val -> val).average().orElse(0.0);
    }


    private StateI<VariablesPole> applyActionUntilTermination(int action, StatePole stateStart) {
        var state = stateStart.copy();
        do {
            stepReturn = environment.step(state,Action.ofInteger(action));
            state = stepReturn.state().copy();
        } while (!stepReturn.isTerminal());
        return state;
    }

    private int applyRandomActionUntilTermination(StateI<VariablesPole> stateStart) {
        var state = stateStart.copy();
        Counter counter=new Counter();
        do {
            int randomAction = RandUtils.getRandomIntNumber(0, EnvironmentPole.NOF_ACTIONS);
            stepReturn = environment.step(state, Action.ofInteger(randomAction));
            state = stepReturn.state().copy();
            counter.increase();
        } while (!stepReturn.isTerminal());
        return counter.getCount();
    }


}
