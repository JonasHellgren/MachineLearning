package policy_gradient_problems.environments.maze;

import org.apache.commons.math3.util.Pair;
import policy_gradient_problems.domain.abstract_classes.AgentA;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;

import java.util.List;

public class MazeAgentPPO extends  AgentA<VariablesMaze>
        implements AgentNeuralActorNeuralCriticI<VariablesMaze> {

    NeuralActorMemoryMazeLossPPO actor;
   // NeuralCriticMemoryMaze critic;

    public MazeAgentPPO(StateI<VariablesMaze> state) {
        super(state);

    }

    @Override
    public List<Double> actionProbabilitiesInPresentState() {
        return null;
    }

    @Override
    public Pair<Double, Double> lossActorAndCritic() {
        return null;
    }

    @Override
    public void fitActor(List<List<Double>> inList, List<List<Double>> outList) {

    }

    @Override
    public List<Double> actorOut(StateI<VariablesMaze> state) {
        return null;
    }

    @Override
    public void fitCritic(List<List<Double>> stateValuesList, List<Double> valueTarList) {

    }

    @Override
    public double criticOut(StateI<VariablesMaze> state) {
        return 0;
    }
}
