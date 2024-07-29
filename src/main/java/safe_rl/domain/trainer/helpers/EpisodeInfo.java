package safe_rl.domain.trainer.helpers;

import common.list_arrays.ListUtils;
import common.math.MathUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.trainer.value_objects.Experience;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class EpisodeInfo<V> {

    public static final double VAL_TOL = 1e-5;
    List<Experience<V>> experiences;

    public int nCorrected() {
        return (int) experiences.stream().filter(e -> e.isSafeCorrected()).count();
    }

    public int nNotCorrected() {
        return (int) experiences.stream().filter(e -> !e.isSafeCorrected()).count();
    }

    public int size() {
        return experiences.size();
    }

    public int nIsTerminal() {
        return (int) experiences.stream().filter(e -> e.isTerminalApplied()).count();
    }

    public int nZeroValued() {
        return (int) experiences.stream()
                .filter(e -> MathUtils.isEqualDoubles(e.value(), 0, VAL_TOL)).count();
    }

    public double sumRewards() {
        return ListUtils.sumList(rewards());
    }

    public List<Double> rewards() {
        return experiences.stream().map(e -> e.rewardApplied()).toList();
    }

    public Experience<V> experienceAtTime(int timeStep) {
        return experiences.get(timeStep);
    }

    public Pair<Double, Double> minMaxAppliedAction() {
        return Pair.create(
                ListUtils.findMin(actionsApplied()).orElseThrow(),
                ListUtils.findMax(actionsApplied()).orElseThrow());
    }

    public List<Double> actionsApplied() {
        return experiences.stream()
                .map(e -> e.actionApplied().asDouble()).toList();
    }


    public   Set<Integer> getValuesOfSpecificDiscreteFeature(int featureIndex) {
        List<Integer> grossList = experiences.stream()
                .map(e -> e.state().discreteFeatures()[featureIndex]).toList();
        return new HashSet<>(grossList);
    }


    public   List<Experience<V>> getExperiencesWithDiscreteFeatureValue(int timeChosen, int featureIndex) {
        return experiences.stream()
                .filter(e -> e.state().discreteFeatures()[featureIndex]== timeChosen).toList();
    }


}
