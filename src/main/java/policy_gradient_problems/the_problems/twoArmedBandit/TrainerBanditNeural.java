package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;

@Log
@Getter
public class TrainerBanditNeural extends TrainerAbstractBandit {

    public static final int NUM_IN = 1;
    AgentBanditNeural agent;

    @Builder
    public TrainerBanditNeural(EnvironmentBandit environment,
                               TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = new AgentBanditNeural(parameters.learningRateActor());
    }

    public void train() {
        var returnCalculator = new ReturnCalculator<VariablesBandit>();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            var experienceList = super.getExperiences(agent);
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList, parameters.gamma());

            for (Experience<VariablesBandit> experience : experienceListWithReturns) {
                INDArray oneHotVector = Nd4j.zeros(EnvironmentBandit.NOF_ACTIONS);
                oneHotVector.putScalar(experience.action().asInt(), experience.value());
                agent.fit(oneHotVector);  //there is no state for bandit problems
            }
            tracker.addMeasures(ei, 0, agent.getActionProbabilities());
        }
    }
}
