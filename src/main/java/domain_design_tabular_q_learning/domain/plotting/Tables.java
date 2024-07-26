package domain_design_tabular_q_learning.domain.plotting;

import domain_design_tabular_q_learning.domain.environment.helpers.GridInformerI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadGridInformer;

public record Tables(
        String[][] values,
        String[][] stateActionValues,
        String[][] actions
) {
    public Tables(GridInformerI e) {
        this(new String[e.nX()][e.nY()], new String[e.nX()][e.nY()], new String[e.nX()][e.nY()]);
    }
}