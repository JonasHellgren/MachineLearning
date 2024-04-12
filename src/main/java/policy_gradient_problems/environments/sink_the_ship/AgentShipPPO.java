package policy_gradient_problems.environments.sink_the_ship;

import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.environments.maze.StateMaze;
import policy_gradient_problems.environments.short_corridor.*;

import java.util.List;

import static common.List2ArrayConverter.convertListToDoubleArr;
import static common.List2ArrayConverter.convertListWithListToDoubleMat;
import static common.ListUtils.arrayPrimitiveDoublesToList;

public class AgentShipPPO extends AgentA<VariablesShip>
        implements AgentNeuralActorNeuralCriticI<VariablesShip> {

    NeuralActorMemoryShipLossPPO actor;
    NeuralCriticMemoryShip critic;

    public static AgentShipPPO newDefault() {
        return new AgentShipPPO(StateShip.newFromPos(EnvironmentShip.getRandomPos()),ShipSettings.newDefault());
    }

    public AgentShipPPO(StateI<VariablesShip> state, ShipSettings shipSettings) {
        super(state);
        this.actor= NeuralActorMemoryShipLossPPO.newDefault(shipSettings);
        this.critic=NeuralCriticMemoryShip.newDefault(shipSettings);
    }

    @SneakyThrows
    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        throw  new NoSuchMethodException();
/*
        StateShip state=(StateShip) getState();
        return actorOut(state);
*/
    }

    @Override
    public Pair<Double,Double> lossActorAndCritic() {
        return Pair.create(actor.getError(),critic.getError());
    }

    @SneakyThrows
    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        actor.fit(convertListWithListToDoubleMat(inList),
                convertListWithListToDoubleMat(outList));
    }

    @Override
    public List<Double> actorOut(StateI<VariablesShip> state) {
        StateShip stateCasted = (StateShip) state;  //todo fimpa nr Statei har asArray
        return arrayPrimitiveDoublesToList(actor.getOutValue(stateCasted.asArray()));
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList) {
        critic.fit(convertListWithListToDoubleMat(stateValuesList),
                convertListToDoubleArr(valueTarList));
    }

    @Override
    public double criticOut(StateI<VariablesShip> state) {
        StateShip stateCasted = (StateShip) state;  //todo fimpa nr Statei har asArray
        return critic.getOutValue(stateCasted.asArray());
    }


    
}
