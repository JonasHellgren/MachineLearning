package policy_gradient_problems.environments.cart_pole;

import common_dl4j.Dl4JUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.helpers.MultiStepNeuralCriticUpdater;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.domain.value_classes.MultiStepResults;
import policy_gradient_problems.helpers.NStepReturnInfo;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.helpers.NeuralActorUpdater;

import java.util.ArrayList;
import java.util.List;

import static common.Conditionals.executeIfTrue;

@Log
@Getter
public class TrainerMultiStepNeuralActorNeuralCriticPole extends TrainerAbstractPole {

    AgentNeuralActorNeuralCriticI<VariablesPole> agent;

    @Builder
    public TrainerMultiStepNeuralActorNeuralCriticPole(@NonNull EnvironmentPole environment,
                                             @NonNull AgentNeuralActorNeuralCriticI<VariablesPole> agent,
                                             @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
    }

    @Override
    public void train() {
        var cu = new MultiStepNeuralCriticUpdater<>(parameters, agent);
        var au=new NeuralActorUpdater<>(agent);
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            var experiences = super.getExperiences(agent);
            var multiStepResults = cu.getMultiStepResults(experiences);
            cu.updateCritic(multiStepResults);
            au.updateActor(multiStepResults);
            printIfSuccessful(ei, experiences);
            updateTracker(ei, experiences);
        }
    }

    private void setStartStateInAgent() {
        agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
    }


    void printIfSuccessful(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }
}
