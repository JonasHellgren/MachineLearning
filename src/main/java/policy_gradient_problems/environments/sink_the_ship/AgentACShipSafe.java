package policy_gradient_problems.environments.sink_the_ship;

import common.math.MathUtils;
import lombok.SneakyThrows;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorTabCriticI;

import java.util.List;

public class AgentACShipSafe  implements AgentParamActorTabCriticI<VariablesShip>  {

    AgentShipParam agentNonSafe;

    public static AgentACShipSafe newRandomStartStateDefaultThetas() {
        return new AgentACShipSafe();
    }

    public AgentACShipSafe() {
        this.agentNonSafe=AgentShipParam.newRandomStartStateDefaultThetas();
    }

    @SneakyThrows
    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        throw new NoSuchMethodException();
    }

    @Override
    public Action chooseAction() {
        Action a=agentNonSafe.chooseAction();
        return  Action.ofDouble(MathUtils.clip(a.doubleValue().orElseThrow(),0.1,0.8));
    }

    @Override
    public StateI<VariablesShip> getState() {
        return agentNonSafe.getState();
    }

    @Override
    public void setState(StateI<VariablesShip> state) {
        agentNonSafe.setState(state);
    }

    @Override
    public void changeActor(RealVector change) {
        agentNonSafe.changeActor(change);
    }

    @Override
    public ArrayRealVector calcGradLogVector(StateI<VariablesShip> state, Action action) {
        return agentNonSafe.calcGradLogVector(state,action);
    }

    @Override
    public void changeCritic(int key, double change) {
        agentNonSafe.changeCritic(key,change);
    }

    @Override
    public double getCriticValue(int key) {
        return agentNonSafe.getCriticValue(key);
    }

    @Override
    public Pair<Double, Double> meanAndStd(int state) {
        return agentNonSafe.meanAndStd(state);
    }
}
