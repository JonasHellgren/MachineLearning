package policy_gradient_problems.the_problems.cart_pole;

import common.ListUtils;
import common_dl4j.Dl4JUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.common_episode_trainers.MultistepNeuralCriticUpdater;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.NStepReturnInfo;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;

import static common.Conditionals.executeIfTrue;

@Log
public class TrainerMultiStepNeuralActorNeuralCriticPole extends TrainerAbstractPole {

    public static final int NOF_ACTIONS = 2;
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
            MultistepNeuralCriticUpdater.MultiStepResults msRes= cu.updateCritic(experiences);

            for (int i = 0; i < msRes.nofSteps ; i++) {
                var in=msRes.stateValuesList.get(i);
                int actionInt = msRes.actionList.get(i).asInt();
                double adv=msRes.valueTarList.get(i)-msRes.valuePresentList.get(i);
                List<Double> oneHot = Dl4JUtil.createListWithOneHotWithValue(NOF_ACTIONS, actionInt,adv);
                oneHot.set(actionInt, adv);
                agent.fitActor(in, oneHot);
            }

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
