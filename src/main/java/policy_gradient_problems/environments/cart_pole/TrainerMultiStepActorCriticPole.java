package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.abstract_classes.ActorUpdaterI;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.helpers.NeuralCriticUpdater;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.MultiStepResultsGenerator;
import policy_gradient_problems.helpers.MultiStepReturnEvaluator;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;

import static common.Conditionals.executeIfTrue;

/***
 * Start state for every episode is deterministic to enable interpretation training evolution,
 * i.e. of nof-steps vs episode.
 *
 */

@Log
@Getter
public class TrainerMultiStepActorCriticPole extends TrainerAbstractPole {

    AgentNeuralActorNeuralCriticI<VariablesPole> agent;
    ActorUpdaterI<VariablesPole> actorUpdater;

    @Builder
    public TrainerMultiStepActorCriticPole(@NonNull EnvironmentPole environment,
                                           @NonNull AgentNeuralActorNeuralCriticI<VariablesPole> agent,
                                           @NonNull TrainerParameters parameters,
                                           @NonNull ActorUpdaterI<VariablesPole> actorUpdater) {
        super(environment, parameters);
        this.agent = agent;
        this.actorUpdater=actorUpdater;
    }

    @Override
    public void train() {
        var msg=new MultiStepResultsGenerator<>(parameters,agent);
        var cu = new NeuralCriticUpdater<>(agent);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            var experiences = super.getExperiences(agent);
            var msr = msg.generate(experiences);
            if (msr.tEnd()<1) {
                log.warning("tEnd zero or below");
            }
                cu.updateCritic(msr);
                actorUpdater.updateActor(msr,agent);
                printIfSuccessful(ei, experiences);
                updateTracker(experiences);
        }
    }

    private void setStartStateInAgent() {
        agent.setState(StatePole.newUprightAndStill(environment.getParameters()));
    }


    void printIfSuccessful(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new MultiStepReturnEvaluator<>(parameters,experiences);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }
}
