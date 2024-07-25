package domain_design_tabular_q_learning.environments.avoid_obstacle;

import lombok.Builder;
import lombok.With;
import org.apache.commons.math3.util.Pair;

@Builder
public record PropertiesRoad(
                               @With Pair<Integer,Integer> minMaxX,
                               Pair<Integer,Integer> minMaxY,
                               Double rewardFailTerminalExp,
                               @With Double rewardFailTerminalStd,
                               Double rewardNonFailTerminal,
                               Double rewardMove,
                               Integer xTerminal,
                               Integer yFailTerminal,
                               Integer yNonFailTerminal,
                               Pair<Integer, Integer> startXMinMax,
                               Pair<Integer, Integer> startYMinMax
) {

    public static PropertiesRoad roadMaze() {
        return PropertiesRoad.builder()
                .minMaxX(Pair.create(0,3)).minMaxY(Pair.create(0,1))
                .rewardFailTerminalExp(-100d).rewardFailTerminalStd(0d)
                .rewardNonFailTerminal(0d).rewardMove(-1d)
                .xTerminal(3).yFailTerminal(1).yNonFailTerminal(0)
                .startXMinMax(Pair.create(0, 0))
                .startYMinMax(Pair.create(0, 1))
                .build();
    }

}
