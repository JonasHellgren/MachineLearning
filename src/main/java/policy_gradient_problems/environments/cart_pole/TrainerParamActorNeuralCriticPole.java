package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.domain.agent_interfaces.AgentParamActorNeuralCriticI;
import policy_gradient_problems.helpers.MultiStepResultsGenerator;
import policy_gradient_problems.helpers.NeuralCriticUpdater;
import policy_gradient_problems.helpers.MultistepParamActorUpdater;
import policy_gradient_problems.domain.value_classes.Experience;
import policy_gradient_problems.helpers.MultiStepReturnEvaluator;
import policy_gradient_problems.domain.value_classes.TrainerParameters;

import java.util.List;

import static common.Conditionals.executeIfTrue;

/***
 *  Details in
 *  https://medium.com/intro-to-artificial-intelligence/the-actor-critic-reinforcement-learning-algorithm-c8095a655c14
 *  and pseudocode in md file pseudocode_pgrl.md
 *
 *  Thanks to the multi-step critic training, a balance between variance and bias can be achieved.
 *  Many steps (large stepHorizon) gives high variance, while few steps gives higher bias.
 *
 */

@Log
@Getter
public final class TrainerParamActorNeuralCriticPole extends TrainerAbstractPole {

    AgentParamActorNeuralCriticI<VariablesPole> agent;

    @Builder
    public TrainerParamActorNeuralCriticPole(@NonNull EnvironmentPole environment,
                                             @NonNull AgentParamActorNeuralCriticI<VariablesPole> agent,
                                             @NonNull TrainerParameters parameters) {
        super(environment, parameters);
        this.agent = agent;
    }

    @Override
    public void train() {
        var msg = new MultiStepResultsGenerator<>(parameters, agent);
        var cu = new NeuralCriticUpdater<>(agent);
        var au = createActorUpdater();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            var experiences = super.getExperiences(agent);
            var multiStepResults = msg.generate(experiences);
            cu.updateCritic(multiStepResults);
            au.updateActor(experiences);
            printIfSuccessFul(ei, experiences);
            updateTracker(ei, experiences);
        }
    }

    private void setStartStateInAgent() {
        agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
    }

    private MultistepParamActorUpdater<VariablesPole> createActorUpdater() {
        return MultistepParamActorUpdater.<VariablesPole>builder()
                .parameters(parameters)
                .criticOut((s) -> agent.getCriticOut(s))
                .calcGradLogVector((s, a) -> agent.calcGradLogVector(s, a))
                .changeActor((rv) -> agent.changeActor(rv)).build();
    }

    void printIfSuccessFul(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new MultiStepReturnEvaluator<>(experiences, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }


}
