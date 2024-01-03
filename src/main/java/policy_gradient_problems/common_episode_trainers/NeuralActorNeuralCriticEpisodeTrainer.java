package policy_gradient_problems.common_episode_trainers;

import common.ListUtils;
import lombok.Builder;
import lombok.NonNull;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.agent_interfaces.AgentNeuralActorNeuralCriticI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_helpers.ReturnCalculator;
import policy_gradient_problems.common_value_classes.TrainerParameters;

import java.util.List;
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
    //    System.out.println("new episode");
        for (Experience<V> experience : elwr) {
            double tdError = calcTdError(experience);
            List<Double> stateAsList = experience.state().asList();
            double criticOut = getCriticOut(experience);
            double valTar = criticOut + tdError;
            agent.fitCritic(List.of(stateAsList), List.of(valTar),1);

            List<Double> oneHot = createOneHot(experience, experience.value());  //todo

            //List<Double> oneHot = createOneHot(experience, tdError);
            agent.fitActor(stateAsList, oneHot);

       //     System.out.println("experience = " + experience);
       //     System.out.println("stateAsList = " + stateAsList+", action = "+experience.action().asInt()+", criticOut = +"+criticOut+", valTar = "+valTar+", oneHot = "+oneHot);

        }
    }

    ////todo code duplic in NeuralActorEpisodeTrainer
    private List<Double> createOneHot(Experience<V> experience, double tdError) {
        int actionInt = experience.action().asInt();
        throwIfNonValidAction(actionInt);
        List<Double> out= ListUtils.createListWithEqualElementValues(nofActions,0d);
        out.set(actionInt, tdError);
        return out;
    }

    private void throwIfNonValidAction(int actionInt) {
        if (actionInt >=nofActions) {
            throw new IllegalArgumentException("Non valid action, actionInt = "+ actionInt);
        }
    }

    private double calcTdError(Experience<V> experience) {
        double v = getCriticOut(experience);
        double vNext = isTerminal.apply(experience.stateNext())
                ? valueTermState
                : agent.getCriticOut(experience.stateNext());
        return experience.reward() + parameters.gamma() * vNext - v;
    }

    private double getCriticOut(Experience<V> experience) {
        return agent.getCriticOut(experience.state());
    }


}
