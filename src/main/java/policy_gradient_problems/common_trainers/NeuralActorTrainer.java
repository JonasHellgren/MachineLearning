package policy_gradient_problems.common_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.abstract_classes.AgentNeuralActorI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.twoArmedBandit.EnvironmentBandit;
import java.util.List;

@Log
@AllArgsConstructor
public class NeuralActorTrainer <V> {

    AgentNeuralActorI<V> agent;
    TrainerParameters parameters;

    public void trainFromEpisode(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        for (Experience<V> experience : elwr) {
            INDArray in=Nd4j.create(agent.getState().asList());

            INDArray oneHotVector = Nd4j.zeros(EnvironmentBandit.NOF_ACTIONS);
            oneHotVector.putScalar(experience.action().asInt(), experience.value());
            agent.fitActor(in,oneHotVector);
        }
    }

    private void logging(Experience<V> experience, RealVector changeInThetaVector) {
        log.fine("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }


}
