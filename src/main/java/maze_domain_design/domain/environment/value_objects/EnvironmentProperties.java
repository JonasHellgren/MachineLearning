package maze_domain_design.domain.environment.value_objects;

import lombok.Builder;
import lombok.With;
import org.apache.commons.math3.util.Pair;

@Builder
public record EnvironmentProperties(
        @With Pair<Integer,Integer> minMaxX,
        Pair<Integer,Integer> minMaxY,
        Double rewardFailTerminal,
        Double rewardNonFailTerminal,
        Double rewardMove,
        Integer xTerminal,
        Integer yFailTerminal,
        Integer yNonFailTerminal
) {

    public static EnvironmentProperties roadMaze() {
        return EnvironmentProperties.builder()
                .minMaxX(Pair.create(0,3)).minMaxY(Pair.create(0,1))
                .rewardFailTerminal(-100d).rewardNonFailTerminal(0d).rewardMove(-1d)
                .xTerminal(3).yFailTerminal(1).yNonFailTerminal(0)
                .build();
    }

}