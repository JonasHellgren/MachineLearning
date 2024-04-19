package safe_rl.helpers;

import common.list_arrays.ListUtils;
import common.math.MathUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import safe_rl.domain.value_classes.Experience;

import java.util.List;

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


    public Pair<Double, Double> minMaxAppliedAction() {
        return Pair.create(
                ListUtils.findMin(actionsApplied()).orElseThrow(),
                ListUtils.findMax(actionsApplied()).orElseThrow());
    }

    public List<Double> actionsApplied() {
        return experiences.stream()
                .map(e -> e.actionApplied().asDouble()).toList();
    }

}
