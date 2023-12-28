package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.common_value_classes.ExperienceDiscreteAction;
import policy_gradient_problems.common.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;

/***
  agent.theta <- agent.theta+learningRateActor*gradLog*vt;
 */

@Log
public class TrainerBanditRealVector extends TrainerAbstractBandit {

    AgentBanditRealVector agent;

    @Builder
    public TrainerBanditRealVector(EnvironmentBandit environment,
                                   AgentBanditRealVector agent,
                                   TrainerParameters parameters) {
        super(environment, parameters);
        this.agent=agent;
    }


    public void train() {
        var returnCalculator=new ReturnCalculator();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            var experienceList = getExperiences(agent);
            var experienceListWithReturns =
                    returnCalculator.createExperienceListWithReturns(experienceList, parameters.gamma());
            for (ExperienceDiscreteAction experience:experienceListWithReturns) {
                var gradLogVector = agent.calcGradLogVector(experience.action());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * vt);
                logging(experience, changeInThetaVector);
                agent.setThetaVector(agent.getThetaVector().add(changeInThetaVector));
            }
            tracker.addMeasures(ei,0,agent.actionProbabilities());
        }
    }

    private void logging(ExperienceDiscreteAction experience, RealVector changeInThetaVector) {
        log.fine("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }



}
