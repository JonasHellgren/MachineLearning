package multi_agent_rl.helpers;

import common.list_arrays.ListUtils;
import common.math.MathUtils;
import lombok.AllArgsConstructor;
import multi_agent_rl.domain.value_classes.Experience;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class EpisodeInfo<V,O> {

    public static final double VAL_TOL = 1e-5;
    List<Experience<V,O>> experiences;

    public int size() {
        return experiences.size();
    }
/*

    public int nIsTerminal() {
        return (int) experiences.stream().filter(e -> e.()).count();
    }
*/

    public int nZeroValued() {
        return (int) experiences.stream()
                .filter(e -> MathUtils.isEqualDoubles(e.value(), 0, VAL_TOL)).count();
    }

    public double sumRewards() {
        return ListUtils.sumList(rewards());
    }

    public List<Double> rewards() {
        return experiences.stream().map(e -> e.reward()).toList();
    }

    public Experience<V,O> experienceAtTime(int timeStep) {
        return experiences.get(timeStep);
    }


/*
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
*/


}
