package book_rl_explained.lunar_lander.helpers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecorderTrainingProgress {

    @Getter
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
            case "sumRewards" -> measuresList.stream().map(pm -> pm.sumRewards()).toList();
            case "sumRewardsNotExploring" -> measuresList.stream().map(pm -> pm.sumRewardsNoExploring()).toList();
            case "nSteps" -> measuresList.stream().map(pm -> (double) pm.nSteps()).toList();
            case "tdError" -> measuresList.stream().map(pm -> pm.tdError()).toList();
            case "stdActor" -> measuresList.stream().map(pm -> pm.stdActor()).toList();
            default -> throw new IllegalArgumentException("Invalid day");
        };
    }

    public int nSteps() {
        return measuresList.size();
    }
}
