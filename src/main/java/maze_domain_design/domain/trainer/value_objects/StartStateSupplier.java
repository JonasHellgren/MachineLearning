package maze_domain_design.domain.trainer.value_objects;

import maze_domain_design.domain.environment.value_objects.PropertiesRoadMaze;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import org.apache.commons.lang3.RandomUtils;

public record StartStateSupplier(
        TrainerProperties properties,
        PropertiesRoadMaze envProperties
) {

    public StateRoad getStartState() {
        var xMinMax=properties.startXMinMax();
        var yMinMax=properties.startYMinMax();
        return StateRoad.of(
                RandomUtils.nextInt(xMinMax.getFirst(),xMinMax.getSecond()+1),
                RandomUtils.nextInt(yMinMax.getFirst(),yMinMax.getSecond()+1),
                envProperties);
    }
}
