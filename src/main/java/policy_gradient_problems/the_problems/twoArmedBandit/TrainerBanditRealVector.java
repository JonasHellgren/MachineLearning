package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.extern.java.Log;
import org.apache.commons.math3.linear.RealVector;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.ReturnCalculator;
import policy_gradient_problems.common_trainers.ParamActorTrainer;
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

        var rc=new ReturnCalculator<VariablesBandit>();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            var experienceList = getExperiences(agent);
            ParamActorTrainer<VariablesBandit> episodeTrainer= new ParamActorTrainer<>(agent,parameters);
            episodeTrainer.trainFromEpisode(experienceList);
            super.tracker.addMeasures(ei,0,agent.actionProbabilities());
        }
    }

    public void trainOld() {
        var rc=new ReturnCalculator<VariablesBandit>();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            var experienceList = getExperiences(agent);
            var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
            for (Experience<VariablesBandit> experience:elwr) {
                var gradLogVector = agent.calcGradLogVector(experience.action().asInt());
                double vt = experience.value();
                var changeInThetaVector = gradLogVector.mapMultiplyToSelf(parameters.learningRateActor() * vt);
                logging(experience, changeInThetaVector);
                agent.changeActor(changeInThetaVector);
            }
            super.tracker.addMeasures(ei,0,agent.actionProbabilities());
        }
    }

    private void logging(Experience<VariablesBandit> experience, RealVector changeInThetaVector) {
        log.fine("experience = " + experience +
                ", changeInThetaVector = " + changeInThetaVector);
    }



}
