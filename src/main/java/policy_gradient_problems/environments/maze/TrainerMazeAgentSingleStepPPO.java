package policy_gradient_problems.environments.maze;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticPPOTrainer;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;

import java.util.List;

@Log
public class TrainerMazeAgentSingleStepPPO  extends TrainerAbstractMaze {

    AgentNeuralActorNeuralCriticI<VariablesMaze> agent;
    MazeSettings mazeSettings;

    @Builder
    public TrainerMazeAgentSingleStepPPO(@NonNull EnvironmentMaze environment,
                                       @NonNull AgentNeuralActorNeuralCriticI<VariablesMaze> agent, @NonNull MazeSettings mazeSettings,
                                       @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
        this.mazeSettings=mazeSettings;
    }


    @Override
    public void train() {
        var episodeTrainer = ActorCriticPPOTrainer.<VariablesMaze>builder()
                .agent(agent)
                .parameters(parameters)
                .valueTermState(0d)
                .nofActions(Direction.values().length)
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            List<Experience<VariablesMaze>> experiences = getExperiences(agent);
            var endExp=experiences.get(experiences.size()-1);
            if (endExp.isTerminal()) {
                episodeTrainer.trainAgentFromExperiences(experiences);
                updateRecorders(agent.lossActorAndCritic());
            } else {
                log.info("Skipped training - not ending in terminal state, ei="+ei);
            }
        }
    }

    private void setStartStateInAgent() {
        agent.setState(StateMaze.randomNotObstacleAndNotTerm(mazeSettings));
    }

}
