package monte_carlo_tree_search.domains.cart_pole;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

public class EnvironmentCartPole implements EnvironmentGenericInterface<CartPoleVariables,Integer> {
    public final double GRAVITY = 9.8;
    public final double MASSCART = 1.0;  //1.0
    public final double MASSPOLE = 0.1;
    public final double TOTAL_MASS = MASSPOLE + MASSCART;
    public final double LENGTH = 0.5;  // actually half the pole's length
    public final double POLEMASS_TIMES_LENGTH = MASSPOLE * LENGTH;
    public final double FORCE_MAG = 10.0;
    public final double TAU = 0.02;  // seconds between state updates  0.02
    public final double THETA_THRESHOLD_RADIANS  = 12 * 2 * 3.141592 / 360;
    public final double X_TRESHOLD = 2;
    public  int MAX_NOF_STEPS =200;
    public  double NON_TERMINAL_REWARD = 1.0;

    @Override
    public StepReturnGeneric<CartPoleVariables> step(ActionInterface<Integer> action, StateInterface<CartPoleVariables> state) {
        double theta=state.getVariables().theta;
        double costheta = Math.cos(theta);
        double sintheta = Math.sin(theta);
        double x=state.getVariables().x;
        double xDot=state.getVariables().xDot;
        double thetaDot=state.getVariables().thetaDot;
        int nofSteps=state.getVariables().nofSteps;

        //For the interested reader: https://coneural.org/florian/papers/05_cart_pole.pdf
        double force=(action.getValue()==0)?-FORCE_MAG:FORCE_MAG;
        double temp = getTempVariable(force, sintheta, thetaDot);
        double thetaacc = getThetaacc(costheta, sintheta, temp);
        double xacc = getXacc(costheta, temp, thetaacc);

        x = x + TAU * xDot;
        xDot = xDot + TAU * xacc;
        theta = theta + TAU * thetaDot;
        thetaDot = thetaDot + TAU * thetaacc;

        StateInterface<CartPoleVariables> newState= new StateCartPole(CartPoleVariables.builder()
                .theta(theta).x(x).thetaDot(thetaDot).xDot(xDot).nofSteps(nofSteps).build());
        boolean isFail=isFailsState(newState);
        boolean isTerminalState=isTerminalState(newState);
        double reward = (isFailsState(newState))?0:NON_TERMINAL_REWARD;

        return StepReturnGeneric.<CartPoleVariables>builder()
                .newState(newState)
                .isFail(isFail)
                .isTerminal(isTerminalState)
                .reward(reward)
                .build();
    }

    private double getTempVariable(double force, double sintheta, double thetaDot) {
        return force + POLEMASS_TIMES_LENGTH * Math.pow(thetaDot,2) * sintheta;
    }

    private double getXacc(double costheta, double temp, double thetaacc) {
        return temp - POLEMASS_TIMES_LENGTH * thetaacc * costheta / TOTAL_MASS;
    }

    private double getThetaacc(double costheta, double sintheta, double temp) {
        return (GRAVITY * sintheta - costheta * temp) / (
                LENGTH * (4.0 / 3.0 - MASSPOLE * Math.pow(costheta,2) / TOTAL_MASS));
    }

    public boolean isTerminalState(StateInterface<CartPoleVariables> state) {
        return (isFailsState(state) |
                state.getVariables().nofSteps >= MAX_NOF_STEPS);
    }

    public boolean isFailsState(StateInterface<CartPoleVariables> state)
    {
        return (state.getVariables().x >= X_TRESHOLD |
                state.getVariables().x <= -X_TRESHOLD |
                state.getVariables().theta >= THETA_THRESHOLD_RADIANS |
                state.getVariables().theta <=-THETA_THRESHOLD_RADIANS);
    }


}
