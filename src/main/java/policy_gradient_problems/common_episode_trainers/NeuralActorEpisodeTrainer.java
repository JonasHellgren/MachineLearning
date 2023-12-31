package policy_gradient_problems.common_episode_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import java.util.List;

@Log
@AllArgsConstructor
public class NeuralActorEpisodeTrainer<V> {

    AgentNeuralActorI<V> agent;
    TrainerParameters parameters;
    int nofActions;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            var in = Nd4j.create(agent.getState().asList());
            var out = createOut(experience);
            agent.fitActor(in, out);
        }
    }

    private INDArray createOut(Experience<V> experience) {
        var oneHotVector = Nd4j.zeros(nofActions);
        oneHotVector.putScalar(experience.action().asInt(), experience.value());
        return oneHotVector;
    }
}
