package policy_gradient_problems.the_problems.twoArmedBandit;

import lombok.Builder;
import lombok.extern.java.Log;
import policy_gradient_problems.common_trainers.ParamActorTrainer;
import policy_gradient_problems.common_value_classes.TrainerParameters;

/***
  agent.theta <- agent.theta+learningRateActor*gradLog*vt;
 */

@Log
public class TrainerBanditParamActor extends TrainerAbstractBandit {

    AgentBanditParamActor agent;

    @Builder
    public TrainerBanditParamActor(EnvironmentBandit environment,
                                   AgentBanditParamActor agent,
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
