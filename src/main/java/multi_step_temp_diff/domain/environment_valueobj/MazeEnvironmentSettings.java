package multi_step_temp_diff.domain.environment_valueobj;

import lombok.Builder;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@Builder
public record MazeEnvironmentSettings(
        int nofCols,
        int nofRows,
        int nofActions,
        double rewardCrash,
        double rewardGoal,
        double rewardMove,
        Set<Pair<Integer,Integer>> obstaclePositions,
        Pair<Integer, Integer> goalPos,
        int maxStepsInEpisode
) {

    public static final int MAX_STEPS_IN_EPISODE = 100;

    public static MazeEnvironmentSettings getDefault() {
        return MazeEnvironmentSettings.builder()
                .nofCols(5)
                .nofRows(6)
                .nofActions(4)
                .rewardCrash(-2)
                .rewardGoal(100)
                .rewardMove(-1)
                .obstaclePositions(new HashSet<>(asList(
                        Pair.create(1,1),Pair.create(2,1),Pair.create(3,1)
                        ,Pair.create(1,3),Pair.create(2,3),Pair.create(3,3)
                        ,Pair.create(2,4))))
                .goalPos(Pair.create(4, 5))
                .maxStepsInEpisode(MAX_STEPS_IN_EPISODE)
                .build();
    }

}
