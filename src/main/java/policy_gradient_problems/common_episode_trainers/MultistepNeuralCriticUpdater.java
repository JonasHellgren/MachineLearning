package policy_gradient_problems.common_episode_trainers;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.Triple;
import policy_gradient_problems.abstract_classes.StateI;
import policy_gradient_problems.common_generic.Experience;
import policy_gradient_problems.common_value_classes.TrainerParameters;
import policy_gradient_problems.common_helpers.NStepReturnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static common.Conditionals.executeIfTrue;

/***
 * The function criticOut and consumer fitCritic is used to make the function generic,
 * would not be fully generic if for example AgentParamActorNeuralCriticI<V> agent is used instead

 *  An episode gives a set of experiences: e0, e1, e2, ei,.....,e_i+n
 *  Two cases are present for e_end: 1) it is fail state 2) not fail state
 *  In the first case the actor should learn from the entire episode, it shall learn from the mistake resulting in fail
 *  But in the second case it shall stop learning after e_end-n. The reason is that the experiences after end-n are
 *  "miss leading". They indicate few rewards remaining, but that is not due to failure but due to non-fail terminal
 *  episode. Many environments stop after a specific amount of steps.

 */

@Log
@AllArgsConstructor
public class MultistepNeuralCriticUpdater<V> {

    TrainerParameters parameters;
    Function<StateI<V>,Double> criticOut;
    Consumer<Triple<List<List<Double>>,List<Double>,Integer>> fitCritic;


    public void updateCritic(Integer n, List<Experience<V>> experiences) {
        int T = experiences.size();
        double gammaPowN = Math.pow(parameters.gamma(), n);
        var elInfo = new NStepReturnInfo<>(experiences, parameters);
        int tEnd = elInfo.isEndExperienceFail() ? T : T - n + 1;  //explained in top of file
        List<List<Double>> stateValuesList = new ArrayList<>();
        List<Double> valueTarList = new ArrayList<>();

        for (int t = 0; t < tEnd; t++) {
            var resMS = elInfo.getResultManySteps(t);
            // executeIfTrue(resMS.isEndOutside(), () -> log.info("Warning isEndOutside"));
            double sumRewards = resMS.sumRewardsNSteps;
            double valueFut = !resMS.isEndOutside  //(resMS.stateFuture().isPresent())
                    //? gammaPowN * agent.getCriticOut(resMS.stateFuture)
                    ? gammaPowN * criticOut.apply(resMS.stateFuture)
                    : 0;
            stateValuesList.add(elInfo.getExperience(t).state().asList());
            valueTarList.add(sumRewards + valueFut);
        }
        int nofFits = (int) Math.max(1,(parameters.relativeNofFitsPerEpoch() * tEnd));  //todo get from record method
//        executeIfTrue(!stateValuesList.isEmpty(), () -> agent.fitCritic(stateValuesList, valueTarList, nofFits));
        executeIfTrue(!stateValuesList.isEmpty(), () -> fitCritic.accept(Triple.of(stateValuesList, valueTarList, nofFits)));

        executeIfTrue(stateValuesList.isEmpty(), () -> log.warning("empty stateValuesList"));
        stateValuesList.clear();
        valueTarList.clear();
    }


    
    
}
