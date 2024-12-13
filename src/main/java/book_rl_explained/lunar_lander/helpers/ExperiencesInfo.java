package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import lombok.AllArgsConstructor;
import org.hellgren.utilities.list_arrays.ListCreator;
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

    public List<Double> forces() {
        return experiences.stream().map(e -> e.action()).toList();
    }

    public List<Double> speeds() {
        return experiences.stream().map(e -> e.state().spd()).toList();
    }

    public List<Double> positions() {
        return experiences.stream().map(e -> e.state().y()).toList();
    }


    public List<Double> times(double dt) {
        int nSteps=nSteps();
        return ListCreator.createFromStartWithStepWithNofItems(0, dt,nSteps);
    }

    public StateLunar stateAtStep(int step) {
        return experiences.get(step).state();
    }
}
