package policy_gradient_problems.the_problems.cart_pole;


import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;

public class TrainerVanillaPole extends TrainerAbstractPole {

    @Builder
    public TrainerVanillaPole(@NonNull EnvironmentPole environment,
                            @NonNull AgentParamActorPole agent,
                            @NonNull TrainerParameters parameters) {
        super(environment, agent, parameters);
    }

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    super.createExperienceListWithReturns(experienceList,parameters.gamma());
            for (Experience<VariablesPole> experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * vt);
                agent.changeActor(changeInThetaVector);

               // agent.setActor(agent.getActor().add(changeInThetaVector));
            }
            updateTracker(ei, experienceList);

        }
    }

}
