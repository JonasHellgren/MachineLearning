package policy_gradient_zeroOrOne.helpers;

import policy_gradient_zeroOrOne.domain.Experience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnCalculator {

    public List<Experience>  createExperienceListWithReturns(List<Experience> experienceList, double gamma) {
        List<Experience> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream().map(e->e.reward()).toList();
        List<Double> returns=calcReturns(rewards,gamma);

        for (Experience exp:experienceList) {
            Experience e = new Experience(exp.action(), exp.reward(), returns.get(experienceList.indexOf(exp)));
            experienceListNew.add(e);
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