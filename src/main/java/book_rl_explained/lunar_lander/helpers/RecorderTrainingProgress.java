package book_rl_explained.lunar_lander.helpers;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hellgren.utilities.math.MovingAverage;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecorderTrainingProgress {

    List<ProgressMeasures> measuresList;

    public static RecorderTrainingProgress empty() {
        return new RecorderTrainingProgress(new ArrayList<>());
    }

    public void add(ProgressMeasures pm) {
        measuresList.add(pm);
    }

    public void clear() {
        measuresList.clear();
    }

    public boolean isEmpty() {
        return measuresList.isEmpty();
    }

    public List<Double> trajectory(String name) {
        var functionHashMap = getStringFunctionHashMap();
        Preconditions.checkArgument(functionHashMap.containsKey(name), "Unknown name: " + name);
        return measuresList.stream().map((functionHashMap.get(name))).toList();
    }

    @NotNull
    private static HashMap<String, Function<ProgressMeasures, Double>> getStringFunctionHashMap() {
        HashMap<String, Function<ProgressMeasures, Double>> map=new HashMap<>();
        map.put("Sum rewards",pm -> pm.sumRewards());
        map.put("stateValuePos2Spd0",ProgressMeasures::stateValuePos2Spd0);
        map.put("stateValuePos5Spd2",ProgressMeasures::stateValuePos5Spd2);
        map.put("nSteps",pm -> (double) pm.nSteps());
        map.put("tdError",ProgressMeasures::tdError);
        map.put("tdErrorClipped",ProgressMeasures::tdErrorClipped);
        map.put("Gradient actor mean",ProgressMeasures::gradMean);
        map.put("Gradient actor mean clipped",ProgressMeasures::gradMeanClipped);
        map.put("Std actor",ProgressMeasures::stdActor);
        map.put("Td error best action",ProgressMeasures::tdBestAction);
        return map;
    }



    public int nSteps() {
        return measuresList.size();
    }
}
