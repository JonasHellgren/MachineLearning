package policy_gradient_problems.the_problems.cart_pole;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.abstract_classes.Action;
import policy_gradient_problems.abstract_classes.AgentA;
import policy_gradient_problems.abstract_classes.AgentParamActorTabCriticI;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common.ParamFunction;
import policy_gradient_problems.common.TabularValueFunction;

import java.util.List;

import static common.ArrayUtil.createArrayWithSameDoubleNumber;

@Setter
@Getter
public class AgentParamActorPole extends AgentA<VariablesPole> implements AgentParamActorTabCriticI<VariablesPole> {
    public static final int LENGTH_THETA = 4;
    public static final double THETA = 1d;

    ParamFunction actor;
    TabularValueFunction critic;
    AgentParamActorPoleHelper helper;

    public static AgentParamActorPole newRandomStartStateDefaultThetas(ParametersPole parameters) {
        return new AgentParamActorPole(StatePole.newAngleAndPosRandom(parameters), getInitThetaVector());
    }

    public static AgentParamActorPole newAllZeroStateDefaultThetas() {
        return new AgentParamActorPole(StatePole.newUprightAndStill(),  getInitThetaVector());
    }

    public AgentParamActorPole(StateI<VariablesPole> stateStart, RealVector thetaVector) {
        super(stateStart);
        this.actor = new ParamFunction(thetaVector);
        this.helper=new AgentParamActorPoleHelper(actor);
    }

    public AgentParamActorPole copy() {
        return new AgentParamActorPole(getState().copy(), actor.copy().asRealVector());
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesPole> state, Action action) {
        return (ArrayRealVector) helper.calcGradLogVector(state,action.asInt());
    }

    @Override
    public List<Double> getActionProbabilities() {
        return helper.calcActionProbabilitiesInState(getState());
    }

    private static ArrayRealVector getInitThetaVector() {
        return new ArrayRealVector(createArrayWithSameDoubleNumber(LENGTH_THETA, THETA));
    }


}
