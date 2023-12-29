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
        ParamActorTrainer<VariablesBandit> episodeTrainer= new ParamActorTrainer<>(agent,parameters);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            episodeTrainer.trainFromEpisode(getExperiences(agent));
            super.tracker.addMeasures(ei,agent.getState().getVariables().arm(),agent.getActionProbabilities());
        }
    }

}
