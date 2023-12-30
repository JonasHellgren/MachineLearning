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
import java.util.function.Function;
import static common.ArrayUtil.createArrayWithSameDoubleNumber;

@Setter
@Getter
public class AgentPole extends AgentA<VariablesPole> implements AgentParamActorTabCriticI<VariablesPole> {
    public static final int LENGTH_THETA = 4;
    public static final double THETA = 1d;

    //StateI<VariablesPole> state;
    //RealVector actor;

    ParamFunction actor;
    TabularValueFunction critic;

    public static AgentPole newRandomStartStateDefaultThetas(ParametersPole parameters) {
        return new AgentPole(StatePole.newAngleAndPosRandom(parameters), getInitThetaVector());
    }

    public static AgentPole newAllZeroStateDefaultThetas() {
        return new AgentPole(StatePole.newUprightAndStill(),  getInitThetaVector());
    }

    public AgentPole(StateI<VariablesPole> stateStart, RealVector thetaVector) {
        super(stateStart);
        this.actor = new ParamFunction(thetaVector);
    }

    public AgentPole copy() {
        return new AgentPole(getState().copy(), actor.copy().asRealVector());
    }

    @Override
    public void changeActor(RealVector change) {
        actor.change(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesPole> state, Action action) {
        return (ArrayRealVector) calcGradLogVector(state,action.asInt());
    }

    @Override
    public List<Double> getActionProbabilities() {
        return calcActionProbabilitiesInState(getState());
    }

    public List<Double> calcActionProbabilitiesInState(StateI<VariablesPole> state) {
        double prob0 = calcProbabilityAction0(state);
        return List.of(prob0,1-prob0);
    }

    public RealVector calcGradLogVector(StateI<VariablesPole> state, int action) {
        var x = state.asRealVector();
        double prob0 = calcProbabilityAction0(state);
        var xTimesProb0= x.mapMultiply(prob0);
        return (action==0)
                ? x.subtract(xTimesProb0)
                : xTimesProb0.mapMultiply(-1d);
    }

    static  Function<Double,Double> logistic=(x) -> Math.exp(x)/(1+Math.exp(x));

    private double calcProbabilityAction0(StateI<VariablesPole> state) {
        double ttx= actor.asRealVector().dotProduct(state.asRealVector());
        return logistic.apply(ttx);
    }

    @NotNull
    private static ArrayRealVector getInitThetaVector() {
        return new ArrayRealVector(createArrayWithSameDoubleNumber(LENGTH_THETA, THETA));
    }


}
