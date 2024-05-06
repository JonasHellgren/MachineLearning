package safe_rl.helpers;

import lombok.AllArgsConstructor;
import safe_rl.domain.value_classes.ExperienceMultiStep;

import java.util.List;

@AllArgsConstructor
public class EpisodeInfoMultiStep<V> {

    public static final double VAL_TOL = 1e-5;
    List<ExperienceMultiStep<V>> experiences;


    public int size() {
        return experiences.size();
    }


    public   List<Integer> getValuesOfSpecificDiscreteFeature(int featureIndex) {
        return experiences.stream()
                .map(e -> e.state().discreteFeatures()[featureIndex]).toList();
    }


    public   List<ExperienceMultiStep<V>> getExperiencesWithDiscreteFeatureValue(int timeChosen, int featureIndex) {
        return experiences.stream()
                .filter(e -> e.state().discreteFeatures()[featureIndex]== timeChosen).toList();
    }


}
