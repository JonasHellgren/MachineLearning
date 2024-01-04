package policy_gradient_problems.common_episode_trainers;

import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.the_problems.short_corridor.EnvironmentSC;
import policy_gradient_problems.the_problems.short_corridor.StateSC;
import policy_gradient_problems.the_problems.short_corridor.VariablesSC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Builder
public class NeuralActorNeuralCriticEpisodeTrainer<V> {
    @NonNull AgentNeuralActorNeuralCriticI<V> agent;
    @NonNull TrainerParameters parameters;
    @NonNull Double valueTermState;
    @NonNull Integer nofActions;
    //@NonNull Function<V, Integer> tabularCoder;  //transforms state to key used by critic function
    @NonNull Function<StateI<V>, Boolean> isTerminal;

    public void trainAgentFromExperiences(List<Experience<V>> experienceList) {
        var rc = new ReturnCalculator<V>();
        var elwr = rc.createExperienceListWithReturns(experienceList, parameters.gamma());
        List<List<Double>> inList = new ArrayList<>();
        List<Double> outList = new ArrayList<>();
        //  System.out.println("episode");

        for (Experience<V> experience : elwr) {
            List<Double> stateAsList = experience.state().asList();
            double vNext = valNext(experience);
            double vTar = experience.reward() + parameters.gamma() * vNext;
            inList.add(stateAsList);
            outList.add(vTar);
            double v = getCriticOut(experience.state());
            double adv = vTar - v;
            agent.fitActor(stateAsList, createOneHot(experience,adv));  //todo

        }
        int nofFits = (int) Math.max(1, (parameters.relativeNofFitsPerEpoch() * experienceList.size()));  //todo get from record method
        agent.fitCritic(inList, outList, nofFits);
        printStateValues();
    }

    private void printStateValues() {
        System.out.println("policy");
        for (int pos = 0; pos < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES; pos++) {
            StateI<VariablesSC> stateSC = StateSC.newFromPos(pos);
            System.out.println("s = " + pos + ", value = " + getCriticOut((StateI<V>) stateSC));
        }
    }

    ////todo code duplic in NeuralActorEpisodeTrainer
    private List<Double> createOneHot(Experience<V> experience, double tdError) {
        int actionInt = experience.action().asInt();
        throwIfNonValidAction(actionInt);
        List<Double> out = ListUtils.createListWithEqualElementValues(nofActions, 0d);
        out.set(actionInt, tdError);
        return out;
    }

    private void throwIfNonValidAction(int actionInt) {
        if (actionInt >= nofActions) {
            throw new IllegalArgumentException("Non valid action, actionInt = " + actionInt);
        }
    }

    private double calcTdError(Experience<V> experience) {
        double v = getCriticOut(experience.state());
        double vNext = valNext(experience);
        return experience.reward() + parameters.gamma() * vNext - v;
    }

    private double valNext(Experience<V> experience) {
        return isTerminal.apply(experience.stateNext())
                ? valueTermState
                : agent.getCriticOut(experience.stateNext());
    }

    private double getCriticOut(StateI<V> state) {
        return agent.getCriticOut(state);
    }


}
