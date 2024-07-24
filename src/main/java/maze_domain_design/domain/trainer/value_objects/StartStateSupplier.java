package maze_domain_design.domain.trainer.value_objects;

import maze_domain_design.domain.environment.value_objects.StateI;
import maze_domain_design.environments.obstacle_on_road.GridVariables;
import maze_domain_design.environments.obstacle_on_road.PropertiesRoad;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import org.apache.commons.lang3.RandomUtils;

public record StartStateSupplier<V>(
        TrainerProperties properties,
        PropertiesRoad envProperties
) {

    public StateI<GridVariables> getStartState() {
        var xMinMax=properties.startXMinMax();
        var yMinMax=properties.startYMinMax();
        return  StateRoad.of(
                RandomUtils.nextInt(xMinMax.getFirst(),xMinMax.getSecond()+1),
                RandomUtils.nextInt(yMinMax.getFirst(),yMinMax.getSecond()+1),
                envProperties);
    }
}
