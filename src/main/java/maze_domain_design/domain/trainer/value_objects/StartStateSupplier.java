package maze_domain_design.domain.trainer.value_objects;

import maze_domain_design.domain.environment.value_objects.EnvironmentProperties;
import maze_domain_design.domain.environment.value_objects.State;
import org.apache.commons.lang3.RandomUtils;

public record StartStateSupplier(
        TrainerProperties properties,
        EnvironmentProperties envProperties
) {

    public State getStartState() {
        var xMinMax=properties.startXMinMax();
        var yMinMax=properties.startYMinMax();
        return State.of(
                RandomUtils.nextInt(xMinMax.getFirst(),xMinMax.getSecond()+1),
                RandomUtils.nextInt(yMinMax.getFirst(),yMinMax.getSecond()+1),
                envProperties);
    }
}
