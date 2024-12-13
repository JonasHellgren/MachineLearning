package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.hellgren.utilities.list_arrays.MyListUtils;
import org.hellgren.utilities.math.MyMathUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EpisodeInfo {
    public static final double VAL_TOL = 1e-5;
    List<ExperienceLunar> experiences;

    public static EpisodeInfo of(List<ExperienceLunar> experiences) {
        return new EpisodeInfo(experiences);
    }



    public int size() {
        return experiences.size();
    }

    public int nIsTerminal() {
        return (int) experiences.stream().filter(e -> e.isTerminal()).count();
    }

/*
    public int nZeroValued() {
        return (int) experiences.stream()
                .filter(e -> MyMathUtils.isEqualDoubles(e.value(), 0, VAL_TOL)).count();
    }
*/

    public double sumRewards() {
        return MyListUtils.sumList(rewards());
    }

    public List<Double> rewards() {
        return experiences.stream().map(e -> e.reward()).toList();
    }

    public ExperienceLunar experienceAtTime(int timeStep) {
        return experiences.get(timeStep);
    }

    public Pair<Double, Double> minMaxAppliedAction() {
        return Pair.create(
                MyListUtils.findMin(actionsApplied()).orElseThrow(),
                MyListUtils.findMax(actionsApplied()).orElseThrow());
    }

    public List<Double> actionsApplied() {
        return experiences.stream()
                .map(e -> e.action()).toList();
    }


/*
    public Set<Integer> getValuesOfSpecificDiscreteFeature(int featureIndex) {
        List<Integer> grossList = experiences.stream()
                .map(e -> e.state().discreteFeatures()[featureIndex]).toList();
        return new HashSet<>(grossList);
    }


    public   List<Experience<V>> getExperiencesWithDiscreteFeatureValue(int timeChosen, int featureIndex) {
        return experiences.stream()
                .filter(e -> e.state().discreteFeatures()[featureIndex]== timeChosen).toList();
    }
*/


}
