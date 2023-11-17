package policy_gradient_problems.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.TrainingTracker;
import policy_gradient_problems.helpers.ReturnCalculator;


@Log
public class TrainerVanillaSC extends TrainerAbstractSC {

    //todo can we avoid boilerplate code below?

    @Builder
    public TrainerVanillaSC(@NonNull EnvironmentSC environment,
                            @NonNull AgentSC agent,
                            @NonNull Integer nofEpisodes,
                            @NonNull Integer nofStepsMax,
                            @NonNull Double gamma,
                            @NonNull Double learningRate) {
        super(environment, agent, nofEpisodes, nofStepsMax, gamma, learningRate, new TrainingTracker());
    }

    public void train() {
        var returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < nofEpisodes; ei++) {
            agent.setStateAsRandomNonTerminal();
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,gamma);
            for (Experience experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(learningRate * vt);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            updateTracker(ei);
        }
    }


}
