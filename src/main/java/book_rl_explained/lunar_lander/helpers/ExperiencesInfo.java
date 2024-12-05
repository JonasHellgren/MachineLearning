package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import lombok.AllArgsConstructor;
import org.hellgren.utilities.list_arrays.MyListUtils;

import java.util.List;

import static org.hellgren.utilities.reinforcement_learning.MyRewardListUtils.discountedElements;
import static org.hellgren.utilities.reinforcement_learning.MyRewardListUtils.getReturns;

@AllArgsConstructor
public class ExperiencesInfo {

    List<ExperienceLunar> experiences;

    public static ExperiencesInfo of(List<ExperienceLunar> experiences) {
        return new ExperiencesInfo(experiences);
    }

    public int nExperiences() {
        return experiences.size();
    }

    public ExperienceLunar endExperience() {
        return experiences.get(nExperiences() - 1);
    }

    public List<Double> rewards() {
        return experiences.stream().map(e -> e.reward()).toList();
    }

    public List<Double> returns(double gamma) {
        return calcReturns(rewards(), gamma);
    }

    private List<Double> calcReturns(List<Double> rewards, double gamma) {
        List<Double> returns=getReturns(rewards);
        return discountedElements(returns,gamma);
    }


    public Double sumRewards() {
        return MyListUtils.sumList(rewards());
    }

    public Integer nSteps() {
        return experiences.size();
    }
}
