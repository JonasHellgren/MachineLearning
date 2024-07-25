package domain_design_tabular_q_learning.domain.plotting;

import domain_design_tabular_q_learning.environments.obstacle_on_road.PropertiesRoad;

public record GridSizeInformer(
        PropertiesRoad ep
) {

    public int nX() {
        return  maxX() - minX() + 1;
    }

    public int nY() {
        return maxY() - minY() + 1;
    }

    public int minY() { return ep.minMaxY().getFirst(); }
    public int maxY() { return ep.minMaxY().getSecond(); }
    public int minX() { return ep.minMaxX().getFirst(); }
    public int maxX() {return  ep.minMaxX().getSecond(); };

}
