package policy_gradient_problems.environments.cart_pole;

import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import common.reinforcment_learning.value_classes.StepReturn;

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
 *          x           Cart Position             -xMax                   xMax
 *          xDot        Cart Velocity             -Inf                    Inf
 *          theta       Pole Angle                -angleMax               angleMax
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

 *     For the interested reader, dynamics details in:
 *     https://coneural.org/florian/papers/05_cart_pole.pdf
*/

public class EnvironmentPole implements EnvironmentI<VariablesPole> {

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

    public StepReturn<VariablesPole> step(StateI<VariablesPole> state0, Action action) {
        StatePole state = (StatePole) state0;
        StatePole newState=state.calcNew(action.asInt(), parameters);
        boolean isFail=isFailsState(newState);
        boolean isTerminalState=isTerminalState(newState);
        double reward = (isFailsState(newState))? parameters.rewardFail() : parameters.rewardNonFail();
        return new StepReturn<>(newState,isFail,isTerminalState,reward);
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
