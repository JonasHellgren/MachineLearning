package domain_design_tabular_q_learning.environments.obstacle_on_road;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import org.apache.commons.lang3.RandomUtils;

public enum ActionRoad implements ActionI<GridActionProperties> {
    N(1,"↑"), E(0,"→"), S(-1,"↓");

    public final int deltaY;
    public final String arrow;

    ActionRoad(int deltaY, String arrow) {
        this.deltaY = deltaY;
        this.arrow=arrow;
    }



}
