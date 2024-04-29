package safe_rl.helpers;

import common.list_arrays.ListUtils;
import common.math.MathUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.ExperienceMultiStep;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class EpisodeInfoMultiStep<V> {

    public static final double VAL_TOL = 1e-5;
    List<ExperienceMultiStep<V>> experiences;


    public int size() {
        return experiences.size();
    }


    public   List<Integer> getValuesOfSpecificDiscreteFeature(int featureIndex) {
        return experiences.stream()
                .map(e -> e.state().discretFeatures()[featureIndex]).toList();
    }


    public   List<ExperienceMultiStep<V>> getExperiencesWithDiscreteFeatureValue(int timeChosen, int featureIndex) {
        return experiences.stream()
                .filter(e -> e.state().discretFeatures()[featureIndex]== timeChosen).toList();
    }


}
