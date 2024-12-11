package book_rl_explained.lunar_lander.helpers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hellgren.utilities.math.MovingAverage;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecorderTrainingProgress {

    public static final int LENGTH_WINDOW = 100;
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

    public List<Double> trajOf(String name) {
        return switch (name) {
            case "sumRewards" ->
                 filter(measuresList.stream().map(pm -> pm.sumRewards()).toList(), LENGTH_WINDOW);
            case "stateValuePos2Spd0" ->
                    measuresList.stream().map(pm -> pm.stateValuePos2Spd0()).toList();
            case "nSteps" ->
                    filter(measuresList.stream().map(pm -> (double) pm.nSteps()).toList(),LENGTH_WINDOW);
            case "tdError" ->
                    filter(measuresList.stream().map(pm -> pm.tdError()).toList(), LENGTH_WINDOW);
            case "stdActor" ->
                    measuresList.stream().map(pm -> pm.stdActor()).toList();
            default -> throw new IllegalArgumentException("Invalid measure");
        };
    }

    private static List<Double> filter(List<Double> inList, int lengthWindow) {
        MovingAverage movingAverage = new MovingAverage(lengthWindow, inList);
        return movingAverage.getFiltered();
    }

    public int nSteps() {
        return measuresList.size();
    }
}
