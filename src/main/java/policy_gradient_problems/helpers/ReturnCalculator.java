package policy_gradient_problems.helpers;

import policy_gradient_problems.common.Experience;
import policy_gradient_problems.common.ExperienceContAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class ReturnCalculator {


    public List<ExperienceContAction>  createExperienceListWithReturnsContActions(List<ExperienceContAction> experienceList,
                                                                                  double gamma) {
/*

        List<Experience> experienceListWithNoReturns=experienceList.stream().map(exp -> Experience.builder()
                .reward(exp.reward()).build()).toList();
        List<Experience> experienceListWithReturns=createExperienceListWithReturns(experienceListWithNoReturns,gamma);
*/


        List<ExperienceContAction> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream().map(e->e.reward()).toList();
        ListIterator<Double> returnsIterator=calcReturns(rewards,gamma).listIterator();

        for (ExperienceContAction exp:experienceList) {
            experienceListNew.add(exp.copyWithValue(returnsIterator.next()));
        }
        return experienceListNew;

    }

    public List<Experience>  createExperienceListWithReturns(List<Experience> experienceList, double gamma) {
        List<Experience> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream().map(e->e.reward()).toList();
        ListIterator<Double> returnsIterator=calcReturns(rewards,gamma).listIterator();

        for (Experience exp:experienceList) {
            experienceListNew.add(exp.copyWithValue(returnsIterator.next()));
        }
        return experienceListNew;
    }

    public List<Double> calcReturns(List<Double> rewards, double gamma) {
        List<Double> rewardsDiscounted=new ArrayList<>();
        double gammaFactor=1d;
        for (double reward:rewards) {
            rewardsDiscounted.add(reward*gammaFactor);
            gammaFactor=gammaFactor*gamma;
        }

        List<Double> rewardsDiscountedAndReversed=new ArrayList<>(rewardsDiscounted);
        Collections.reverse(rewardsDiscountedAndReversed);
        List<Double> returnsReversed=new ArrayList<>();

        double rewardsSum=0;
        for (double reward:rewardsDiscountedAndReversed) {
            rewardsSum+=reward;
            returnsReversed.add(rewardsSum);
        }
        List<Double> returns=new ArrayList<>(returnsReversed);
        Collections.reverse(returns);

        return returns;
    }

}
