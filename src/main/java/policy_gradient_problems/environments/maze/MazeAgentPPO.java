package policy_gradient_problems.environments.maze;

import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.awt.geom.Point2D;
import java.util.List;

import static common.List2ArrayConverter.convertListToDoubleArr;
import static common.List2ArrayConverter.convertListWithListToDoubleMat;
import static common.ListUtils.arrayPrimitiveDoublesToList;

public class MazeAgentPPO extends AgentA<VariablesMaze>
        implements AgentNeuralActorNeuralCriticI<VariablesMaze> {

    NeuralActorMemoryMazeLossPPO actor;
    NeuralCriticMemoryMazeLossPPO critic;

    public static MazeAgentPPO newDefaultAtX0Y0() {
        return new MazeAgentPPO(
                StateMaze.newFromPoint(new Point2D.Double(0, 0))
                , MazeSettings.newDefault());
    }

    public MazeAgentPPO(StateI<VariablesMaze> state, MazeSettings mazeSettings) {
        super(state);
        this.actor = NeuralActorMemoryMazeLossPPO.newDefault(mazeSettings);
        this.critic = NeuralCriticMemoryMazeLossPPO.newDefault(mazeSettings);
    }

    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        StateMaze state = (StateMaze) getState();
        return actorOut(state);
    }

    @Override
    public Pair<Double, Double> lossActorAndCritic() {
        return Pair.create(actor.getError(), critic.getError());
    }

    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {
        actor.fit(convertListWithListToDoubleMat(inList),
                convertListWithListToDoubleMat(outList));
    }

    @Override
    public List<Double> actorOut(StateI<VariablesMaze> state) {
        StateMaze stateCasted = (StateMaze) state;
        return arrayPrimitiveDoublesToList(actor.getOutValue(stateCasted.asArray()));
    }

    public double[] actorOutArr(StateI<VariablesMaze> state) {
        StateMaze stateCasted = (StateMaze) state;
        return actor.getOutValue(stateCasted.asArray());
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList) {
        critic.fit(convertListWithListToDoubleMat(stateValuesList),
                convertListToDoubleArr(valueTarList));
    }

    @Override
    public double criticOut(StateI<VariablesMaze> state) {
        return critic.getOutValue(state);
    }
}
