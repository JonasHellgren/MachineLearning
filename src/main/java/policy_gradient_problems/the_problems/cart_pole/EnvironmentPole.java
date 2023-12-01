package policy_gradient_problems.the_problems.cart_pole;

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
 *     Actions:     0     Push cart to the left,  1     Push cart to the right
  *     Reward:     Reward is 1 for every step taken, including the termination step

 *     For the interested reader, dynamics details in: https://coneural.org/florian/papers/05_cart_pole.pdf
*/

public class EnvironmentPole {

    public static int ACTION_LEFT=0, ACTION_RIGHT=1, NOF_ACTIONS=2;

    ParametersPole parameters;

    public static EnvironmentPole newDefault() {
        return new   EnvironmentPole(ParametersPole.newDefault());
    }
    public EnvironmentPole(ParametersPole parameters) {
        this.parameters = parameters;
    }

    public ParametersPole getParameters() {
        return parameters;
    }

    public StepReturnPole step(int action, StatePole state) {
        var newState=state.calcNew(action, parameters);
        boolean isFail=isFailsState(newState);
        boolean isTerminalState=isTerminalState(newState);
        double reward = (isFailsState(newState))? parameters.rewardFail() : parameters.rewardNonFail();
        return StepReturnPole.builder()
                .newState(newState)
                .isFail(isFail)
                .isTerminal(isTerminalState)
                .reward(reward)
                .build();
    }

    public boolean isTerminalState(StatePole state) {
        return (isFailsState(state) || state.nofSteps() >= parameters.maxNofSteps());
    }

    public boolean isFailsState(StatePole state)
    {
        return (Math.abs(state.x()) >= parameters.xMax() ||
                Math.abs(state.angle()) >= parameters.angleMax());
    }

}
