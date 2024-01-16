package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.agent_interfaces.AgentParamActorNeuralCriticI;
import policy_gradient_problems.common_episode_trainers.MultistepNeuralCriticUpdater;
import policy_gradient_problems.common_episode_trainers.MultistepParamActorUpdater;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.NStepReturnInfo;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

import static common.Conditionals.executeIfTrue;

@Log
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
        var cu = createCriticUpdater();
       // var au = createActorUpdater();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            var experiences = super.getExperiences(agent);
            cu.updateCritic(experiences);


         //   agent.fitActor(inList, oneHot, nofFits);

            //updateActor(experiences);
            printIfSuccessFul(ei, experiences);
            updateTracker(ei, experiences);
        }
    }

    private void setStartStateInAgent() {
        agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
    }

    private MultistepNeuralCriticUpdater<VariablesPole> createCriticUpdater() {
        return new MultistepNeuralCriticUpdater<>(
                parameters,
                (s) -> agent.getCriticOut(s),
                (t) -> agent.fitCritic(t.getLeft(), t.getMiddle(), t.getRight()));
    }

  /*  private MultistepParamActorUpdater<VariablesPole> createActorUpdater() {
        return MultistepParamActorUpdater.<VariablesPole>builder()
                .parameters(parameters)
                .criticOut((s) -> agent.getCriticOut(s))
                .calcGradLogVector((s, a) -> agent.calcGradLogVector(s, a))
                .changeActor((rv) -> agent.changeActor(rv)).build();
    }*/

    void printIfSuccessFul(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }
}
