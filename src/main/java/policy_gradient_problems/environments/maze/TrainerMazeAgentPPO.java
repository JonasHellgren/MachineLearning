package policy_gradient_problems.environments.maze;

import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticPPOTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;

public class TrainerMazeAgentPPO extends TrainerAbstractMaze {

    AgentNeuralActorNeuralCriticI<VariablesMaze> agent;
    MazeSettings mazeSettings;

    public static final double VALUE_TERMINAL_STATE = 0;

    @Builder
    public TrainerMazeAgentPPO(@NonNull EnvironmentMaze environment,
                               @NonNull AgentNeuralActorNeuralCriticI<VariablesMaze> agent,
                               @NonNull TrainerParameters parameters,
                               @NonNull MazeSettings mazeSettings) {
        super(environment, parameters);
        this.agent = agent;
        this.mazeSettings = mazeSettings;
    }

    @Override
    public void train() {
        var episodeTrainer = ActorCriticPPOTrainer.<VariablesMaze>builder()
                .agent(agent)
                .parameters(parameters)
                .valueTermState(VALUE_TERMINAL_STATE)
                .nofActions(EnvironmentSC.NOF_ACTIONS)
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            agent.setState(StateMaze.randomFeasible(mazeSettings));
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateRecorders(agent.lossActorAndCritic());
        }
    }
}
