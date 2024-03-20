package policy_gradient_problems.the_problems.cart_pole;

import common_dl4j.Dl4JUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.common_episode_trainers.MultistepNeuralCriticUpdater;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_generic.MultiStepResults;
import policy_gradient_problems.common_helpers.NStepReturnInfo;
import policy_gradient_problems.common_value_classes.TrainerParameters;

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
        var cu = createCriticUpdater();
        for (int ei = 0; ei < parameters.nofEpisodes(); ei++) {
            setStartStateInAgent();
            var experiences = super.getExperiences(agent);
            var multiStepResults= cu.updateCritic(experiences);
            updateActor(multiStepResults);
            printIfSuccessFul(ei, experiences);
            updateTracker(ei, experiences);
        }
    }

    private void updateActor(MultiStepResults msRes) {
        List<List<Double>> inList=new ArrayList<>();
        List<List<Double>> outList=new ArrayList<>();
        for (int step = 0; step < msRes.nofSteps() ; step++) {
            inList.add(msRes.stateValuesList().get(step));
            outList.add(createOneHot(msRes, step));
        }
        agent.fitActor(inList,outList);
    }

    @NotNull
    private static List<Double> createOneHot(MultiStepResults msRes, int i) {
        int actionInt = msRes.actionList().get(i).asInt();
        double adv= msRes.valueTarList().get(i)- msRes.valuePresentList().get(i);
        List<Double> oneHot = Dl4JUtil.createListWithOneHotWithValue(StatePole.nofActions(), actionInt,adv);
        oneHot.set(actionInt, adv);
        return oneHot;
    }

    private void setStartStateInAgent() {
        agent.setState(StatePole.newAngleAndPosRandom(environment.getParameters()));
    }

    private MultistepNeuralCriticUpdater<VariablesPole> createCriticUpdater() {
        return new MultistepNeuralCriticUpdater<>(
                parameters,
                (s) -> agent.getCriticOut(s),
                (t) -> agent.fitCritic(t.getLeft(), t.getMiddle()));
    }

    void printIfSuccessFul(int ei, List<Experience<VariablesPole>> experiences) {
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        executeIfTrue(!elInfo.isEndExperienceFail(), () ->
                log.info("Episode successful, ei = " + ei + ", n steps = " + experiences.size()));
    }
}
