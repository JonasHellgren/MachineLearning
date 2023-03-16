package monte_carlo_search.mcts_cart_pole;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.cart_pole.ActionCartPole;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.domains.cart_pole.EnvironmentCartPole;
import monte_carlo_tree_search.domains.cart_pole.StateCartPole;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCartPoleEnvironment {

    private static final int VALUE_LEFT = 0;
    private static final int X_INIT = 0;
    private static final int THETA_INIT = 0;
    private static final int THETA_DOT_INIT = 0;
    private static final int X_DOT_INIT = 0;
    private static final int VALUE_RIGHT = 1;
    private static final int NOF_STEPS = 10_000;
    EnvironmentGenericInterface<CartPoleVariables,Integer> environment;
    StateInterface<CartPoleVariables> stateUpRight;
    CartPoleVariables variables;
    ActionCartPole action;
    StepReturnGeneric<CartPoleVariables> stepReturn;

    @Before
    public void init() {
        environment= EnvironmentCartPole.newDefault();
        stateUpRight = new StateCartPole(CartPoleVariables.builder()
                .theta(THETA_INIT)
                .thetaDot(THETA_DOT_INIT)
                .x(X_INIT)
                .xDot(X_DOT_INIT)
                .build());
        variables= stateUpRight.getVariables();
        action= ActionCartPole.builder().rawValue(VALUE_LEFT).build();
    }

    @Test
    public void forceLeftMovesCartLeftAndRotatesRight() {
        ActionCartPole action= ActionCartPole.builder().rawValue(VALUE_LEFT).build();
        applyActionUntilTermination(action);

        System.out.println("state.getVariables() = " + stateUpRight.getVariables());

        Assert.assertTrue(stateUpRight.getVariables().x<X_INIT);
        Assert.assertTrue(stateUpRight.getVariables().theta>THETA_INIT);
        Assert.assertTrue(stateUpRight.getVariables().nofSteps>0);
    }

    @Test
    public void forceRightMovesCartRightAndRotatesLeft() {
        ActionCartPole action= ActionCartPole.builder().rawValue(VALUE_RIGHT).build();
        applyActionUntilTermination(action);

        System.out.println("state.getVariables() = " + stateUpRight.getVariables());

        Assert.assertTrue(stateUpRight.getVariables().x>X_INIT);
        Assert.assertTrue(stateUpRight.getVariables().theta<THETA_INIT);
        Assert.assertTrue(stateUpRight.getVariables().nofSteps>0);
    }

    @Test public void randomAction() {
        List<Integer> actionList=new ArrayList<>();
        for (int i = 0; i < NOF_STEPS ; i++) {
            ActionCartPole action=ActionCartPole.newRandom();
            actionList.add(action.getValue());
        }

        double averageActionValue=actionList.stream().mapToInt(val -> val).average().orElse(0.0);
        Assert.assertEquals(0.5,averageActionValue,0.2);
    }

    @Test
    public void moreStepsFromUpRightComparedToLeaning() {
        StateCartPole stateLeaning = new StateCartPole(CartPoleVariables.builder()
                .theta(EnvironmentCartPole.THETA_THRESHOLD_RADIANS*0.75)
                .thetaDot(THETA_DOT_INIT)
                .x(X_INIT)
                .xDot(X_DOT_INIT)
                .build());

        double averageNofStepsUpRight=calAverageNofStepsForRandomActions(stateUpRight.copy());
        double averageNofStepsLeaning =calAverageNofStepsForRandomActions(stateLeaning.copy());

        System.out.println("averageNofStepsUpRight = " + averageNofStepsUpRight);
        System.out.println("averageNofStepsLeaning = " + averageNofStepsLeaning);
        Assert.assertTrue(averageNofStepsLeaning<averageNofStepsUpRight);
    }

    @Test
    public void moreStepsFromUpRightComparedToRandom() {
        StateCartPole stateRandom = StateCartPole.newRandom();
        System.out.println("stateRandom = " + stateRandom);

        double averageNofStepsUpRight=calAverageNofStepsForRandomActions(stateUpRight.copy());
        double averageNofStepsRandom =calAverageNofStepsForRandomActions(stateRandom.copy());

        System.out.println("averageNofStepsUpRight = " + averageNofStepsUpRight);
        System.out.println("averageNofStepsRandom = " + averageNofStepsRandom);
        Assert.assertTrue(averageNofStepsRandom<averageNofStepsUpRight);
    }

    private double calAverageNofStepsForRandomActions(StateInterface<CartPoleVariables> startState) {
        List<Integer> nofStepsUpRightList=new ArrayList<>();
        for (int i = 0; i < NOF_STEPS; i++) {
            int nofSteps=applyRandomActionUntilTermination( startState.copy());
            nofStepsUpRightList.add(nofSteps);
        }
        return  nofStepsUpRightList.stream().mapToInt(val -> val).average().orElse(0.0);
    }

    private void applyActionUntilTermination(ActionCartPole action) {
        do {
            stepReturn=environment.step(action, stateUpRight);
            stateUpRight.setFromReturn(stepReturn);
        }  while (!stepReturn.isTerminal);
    }

    private int applyRandomActionUntilTermination(StateInterface<CartPoleVariables> state) {
        do {
            ActionCartPole action=ActionCartPole.newRandom();
            stepReturn=environment.step(action,state);
            state.setFromReturn(stepReturn);
        }  while (!stepReturn.isTerminal);
        return state.getVariables().nofSteps;
    }

}
