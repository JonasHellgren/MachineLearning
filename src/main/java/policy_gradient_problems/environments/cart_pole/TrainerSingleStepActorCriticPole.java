package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticCEMLossTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

@Log
public class TrainerSingleStepActorCriticPole extends TrainerAbstractPole {

    AgentActorICriticPoleCEM agent;
    ParametersPole parametersPole;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerSingleStepActorCriticPole(@NonNull EnvironmentPole environment,
                                            @NonNull AgentActorICriticPoleCEM agent,
                                            @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
        this.parametersPole= environment.getParameters();
    }

    @Override
    public void train() {
        ActorCriticCEMLossTrainer<VariablesPole> episodeTrainer =
                ActorCriticCEMLossTrainer.<VariablesPole>builder()
                        .agent(agent)
                        .parameters(parameters)
                        .valueTermState(VALUE_TERMINAL_STATE)
                        .nofActions(2)
                        .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StatePole.newAngleAndPosRandom(parametersPole));
            var experList = getExperiences(agent);
            episodeTrainer.trainAgentFromExperiences(experList);
            var episodeRunner =  PoleAgentOneEpisodeRunner.newOf(environment,agent);
            int nStepsEval= episodeRunner.runTrainedAgent(StatePole.newUprightAndStill(environment.getParameters()));
            updateRecorder(experList,nStepsEval,agent);
            if (experList.size() > 50) {
                    log.info("Episode = "+ei+", nof steps = " + experList.size());
            }
        }
    }
    
}
