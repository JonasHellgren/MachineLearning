package domain_design_tabular_q_learning.environments.avoid_obstacle;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.Getter;

public enum ActionRoad implements ActionI<RoadActionProperties> {
    N(RoadActionProperties.of(1,"↑")),
    E(RoadActionProperties.of(0,"→")),
    S(RoadActionProperties.of(-1,"↓"));

    @Getter  final RoadActionProperties properties;

    ActionRoad(RoadActionProperties properties) {
        this.properties=properties;
    }

    @Override
    public String toString() {
        return properties.arrow();
    }


}
