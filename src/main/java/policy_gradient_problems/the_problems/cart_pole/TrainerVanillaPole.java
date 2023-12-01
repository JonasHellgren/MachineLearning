package policy_gradient_problems.the_problems.cart_pole;


import java.util.List;

public class TrainerVanillaPole extends TrainerAbstractPole {

    public void train() {
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
            var experienceList = getExperiences();
            var experienceListWithReturns =
                    super.createExperienceListWithReturns(experienceList,parameters.gamma());
            for (ExperiencePole experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.state(),experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRate() * vt);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            updateTracker(ei, List.of((double) experienceList.size()));
        }
    }

}
