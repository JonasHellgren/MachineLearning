package monte_carlo_tree_search.domains.cart_pole;

import lombok.Builder;
import lombok.Setter;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

/***
 * Description:
 *         A pole is attached by an un-actuated joint to a cart, which moves along
 *         a frictionless track. The pendulum starts upright, and the goal is to
 *         prevent it from falling over by increasing and reducing the cart's
 *         velocity.
 *     Source:
 *         This environment corresponds to the version of the cart-pole problem
 *         described by Barto, Sutton, and Anderson
 *     Observation:
 *          Term        Observation               Min                     Max
 *          x           Cart Position             -4.8                    4.8
 *          xDot        Cart Velocity             -Inf                    Inf
 *          theta       Pole Angle                -0.418 rad (-24 deg)    0.418 rad (24 deg)
 *          thetaDot    Pole Angular Velocity     -Inf                    Inf
 *
 *
 *                 |theta /
 *                      /
 *                    /
 *              ___ /___
 *             |       |  ----> F
 *          ----------------------------------------- x
 *
 *     Actions:
 *         Type: Discrete(2)
 *         Num   Action
 *         0     Push cart to the left
 *         1     Push cart to the right
 *         Note: The amount the velocity that is reduced or increased is not
 *         fixed; it depends on the angle the pole is pointing. This is because
 *         the center of gravity of the pole increases the amount of energy needed
 *         to move the cart underneath it
 *     Reward:
 *         Reward is 1 for every step taken, including the termination step
 *     Starting State:
 *         All observations are assigned a uniform random value in [-0.05..0.05]
 *     Episode Termination:
 *         Pole Angle is more than 12 degrees.
 *         Cart Position is more than 2.4 (center of the cart reaches the edge of
 *         the display).
 *         Episode length is greater than 200.
 *         Solved Requirements:
 *         Considered solved when the average return is greater than or equal to
 *         195.0 over 100 consecutive trials.
 *
 *     For the interested reader: https://coneural.org/florian/papers/05_cart_pole.pdf
 */
@Builder
public class EnvironmentCartPole implements EnvironmentGenericInterface<CartPoleVariables,Integer> {
    public final double GRAVITY = 9.8;
    public final double MASS_CART = 1.0;  //1.0
    public final double MASS_POLE = 0.1;
    public final double TOTAL_MASS = MASS_POLE + MASS_CART;
    public final double LENGTH = 0.5;  // actually half the pole's length
    public static final double Y_MAX = 1;   //domain limit
    public static final double Y_MIN = -0.5;
    public final double POLE_MASS_TIMES_LENGTH = MASS_POLE * LENGTH;
    public final double FORCE_MAG = 10.0;
    public final double TAU = 0.02;  // seconds between state updates  0.02
    public static final double THETA_THRESHOLD_RADIANS  = 12 * 2 * 3.141592 / 360;
    public static final double X_TRESHOLD = 2;
    public static final int MAX_NOF_STEPS = 200;
    public static final double NON_TERMINAL_REWARD = 1.0;

    @Builder.Default
    public int maxNofSteps=MAX_NOF_STEPS;
    @Builder.Default
    public double nonTerminalReward=NON_TERMINAL_REWARD;

    public static EnvironmentCartPole newDefault() {
        return EnvironmentCartPole.builder().build();
    }

    @Override
    public StepReturnGeneric<CartPoleVariables> step(ActionInterface<Integer> action, StateInterface<CartPoleVariables> state) {
        double theta=state.getVariables().theta;
        double costheta = Math.cos(theta);
        double sintheta = Math.sin(theta);
        double x=state.getVariables().x;
        double xDot=state.getVariables().xDot;
        double thetaDot=state.getVariables().thetaDot;
        int nofSteps=state.getVariables().nofSteps;

        double force=(action.getValue()==0)?-FORCE_MAG:FORCE_MAG;
        double temp = getTempVariable(force, sintheta, thetaDot);
        double thetaacc = getThetaAcc(costheta, sintheta, temp);
        double xacc = getXAcc(costheta, temp, thetaacc);

        x = x + TAU * xDot;
        xDot = xDot + TAU * xacc;
        theta = theta + TAU * thetaDot;
        thetaDot = thetaDot + TAU * thetaacc;
        nofSteps++;

        StateInterface<CartPoleVariables> newState= new StateCartPole(CartPoleVariables.builder()
                .theta(theta).x(x).thetaDot(thetaDot).xDot(xDot).nofSteps(nofSteps).build());
        boolean isFail=isFailsState(newState);
        boolean isTerminalState=isTerminalState(newState);
        double reward = (isFailsState(newState))?0:nonTerminalReward;

        return StepReturnGeneric.<CartPoleVariables>builder()
                .newState(newState)
                .isFail(isFail)
                .isTerminal(isTerminalState)
                .reward(reward)
                .build();
    }

    private double getTempVariable(double force, double sinTheta, double thetaDot) {
        return force + POLE_MASS_TIMES_LENGTH * Math.pow(thetaDot,2) * sinTheta;
    }

    private double getXAcc(double cosTheta, double temp, double thetaAcc) {
        return temp - POLE_MASS_TIMES_LENGTH * thetaAcc * cosTheta / TOTAL_MASS;
    }

    private double getThetaAcc(double costheta, double sintheta, double temp) {
        return (GRAVITY * sintheta - costheta * temp) / (
                LENGTH * (4.0 / 3.0 - MASS_POLE * Math.pow(costheta,2) / TOTAL_MASS));
    }

    public boolean isTerminalState(StateInterface<CartPoleVariables> state) {
        return (isFailsState(state) |
                state.getVariables().nofSteps >= maxNofSteps);
    }

    public boolean isFailsState(StateInterface<CartPoleVariables> state)
    {
        return (state.getVariables().x >= X_TRESHOLD |
                state.getVariables().x <= -X_TRESHOLD |
                state.getVariables().theta >= THETA_THRESHOLD_RADIANS |
                state.getVariables().theta <=-THETA_THRESHOLD_RADIANS);
    }


}
