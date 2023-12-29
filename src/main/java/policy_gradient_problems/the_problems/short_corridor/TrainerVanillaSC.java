package policy_gradient_problems.the_problems.short_corridor;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.common_value_classes.ExperienceOld;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common.ReturnCalculatorOld;


@Log
public class TrainerVanillaSC extends TrainerAbstractSC {

    @Builder
    public TrainerVanillaSC(@NonNull EnvironmentSC environment,
                            @NonNull AgentSC agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }

    public void train() {
        var returnCalculator=new ReturnCalculatorOld();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setStateAsRandomNonTerminal();
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList,parameters.gamma());
            for (ExperienceOld experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * vt);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            updateTracker(ei);
        }
    }


}
