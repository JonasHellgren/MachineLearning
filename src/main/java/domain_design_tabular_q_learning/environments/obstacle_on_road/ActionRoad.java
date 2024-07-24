package domain_design_tabular_q_learning.environments.obstacle_on_road;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

public enum ActionRoad implements ActionI<GridActionProperties> {
    N(GridActionProperties.of(1,"↑")),
    E(GridActionProperties.of(0,"→")),
    S(GridActionProperties.of(-1,"↓"));

/*
    public final int deltaY;
    public final String arrow;
*/

    @Getter  final GridActionProperties properties;

    ActionRoad(GridActionProperties properties) {
        this.properties=properties;
    }

    @Override
    public String toString() {
        return properties.arrow();
    }


}
