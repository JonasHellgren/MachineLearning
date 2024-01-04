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
        List<List<Double>> inList=new ArrayList<>();
        List<Double> outList=new ArrayList<>();
        //System.out.println("episode");
        for (Experience<V> experience : elwr) {
            //double tdError = calcTdError(experience);
            List<Double> stateAsList = experience.state().asList();
            double vNext = getCriticOut(experience.stateNext());
            double vTar = experience.reward()+ parameters.gamma()*vNext ;

            inList.add(stateAsList);
            outList.add(vTar);
            //outList.add(experience.value());

            //List<Double> oneHot = createOneHot(experience, experience.value());  //todo
            double v = getCriticOut(experience.state());
            double adv=vTar-v;
            //double adv=experience.value();
            List<Double> oneHot = createOneHot(experience, adv);
            agent.fitActor(stateAsList, oneHot);

          //  System.out.println("stateAsList = " + stateAsList+", action = "+experience.action()+", oneHot = " + oneHot);

            //     System.out.println("experience = " + experience);
            //     System.out.println("stateAsList = " + stateAsList+", action = "+experience.action().asInt()+", criticOut = +"+criticOut+", valTar = "+valTar+", oneHot = "+oneHot);
        }
        int nofFits = (int) Math.max(1,(parameters.relativeNofFitsPerEpoch() * experienceList.size()));  //todo get from record method
        agent.fitCritic(inList, outList, nofFits);

        printPolicy();
    //    System.out.println("inList = " + inList);
    //    System.out.println("outList = " + outList);

    }

    private void printPolicy() {
        System.out.println("policy");
        for (int pos = 0; pos < EnvironmentSC.NOF_NON_TERMINAL_OBSERVABLE_STATES ; pos++) {
            StateI<VariablesSC> stateSC = StateSC.newFromPos(pos);
            System.out.println("s = "+pos+", value = "+getCriticOut((StateI<V>) stateSC));
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
