package domain_design_tabular_q_learning.environments.avoid_obstacle;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.Getter;

public enum ActionRoad implements ActionI<GridActionProperties> {
    N(GridActionProperties.of(1,"↑")),
    E(GridActionProperties.of(0,"→")),
    S(GridActionProperties.of(-1,"↓"));

    @Getter  final GridActionProperties properties;

    ActionRoad(GridActionProperties properties) {
        this.properties=properties;
    }

    @Override
    public String toString() {
        return properties.arrow();
    }


}
