package policy_gradient_problems.environments.maze;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.abstract_classes.ActorUpdaterI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.domain.common_episode_trainers.ActorCriticPPOTrainer;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.VariablesPole;
import policy_gradient_problems.environments.short_corridor.EnvironmentSC;
import policy_gradient_problems.helpers.MultiStepResultsGenerator;
import policy_gradient_problems.helpers.NeuralCriticUpdater;

@Log
public class TrainerMazeAgentPPO extends TrainerAbstractMaze {
    public static final double VALUE_TERMINAL_STATE = 0;

    AgentNeuralActorNeuralCriticI<VariablesMaze> agent;
    MazeSettings mazeSettings;
    ActorUpdaterI<VariablesMaze> actorUpdater;

    @Builder
    public TrainerMazeAgentPPO(@NonNull EnvironmentMaze environment,
                               @NonNull AgentNeuralActorNeuralCriticI<VariablesMaze> agent,
                               @NonNull TrainerParameters parameters,
                               @NonNull MazeSettings mazeSettings,
                               @NonNull ActorUpdaterI<VariablesMaze> actorUpdater) {
        super(environment, parameters);
        this.agent = agent;
        this.mazeSettings = mazeSettings;
        this.actorUpdater=actorUpdater;
    }

    @Override
    public void train() {
        var msg=new MultiStepResultsGenerator<>(parameters,agent,true);
        var cu = new NeuralCriticUpdater<>(agent);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            var experiences = super.getExperiences(agent);
            var msr = msg.generate(experiences);
            if (msr.tEnd()<1) {
                log.warning("tEnd zero or below");
                log.fine(experiences.toString());
                log.fine(msr.toString());
            } else {
              //  System.out.println("msr = " + msr);
                cu.updateCritic(msr);
                actorUpdater.updateActor(msr, agent);
                updateRecorders(agent.lossActorAndCritic());
            }

        }
    }

    public void trainOld() {
        var episodeTrainer = ActorCriticPPOTrainer.<VariablesMaze>builder()
                .agent(agent)
                .parameters(parameters)
                .valueTermState(VALUE_TERMINAL_STATE)
                .nofActions(EnvironmentSC.NOF_ACTIONS)
                .build();

        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            episodeTrainer.trainAgentFromExperiences(getExperiences(agent));
            updateRecorders(agent.lossActorAndCritic());
        }
    }

    private void setStartStateInAgent() {
        agent.setState(StateMaze.randomNotObstacleAndNotTerm(mazeSettings));
    }
}
