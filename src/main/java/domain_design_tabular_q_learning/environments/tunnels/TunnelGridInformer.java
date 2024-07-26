package domain_design_tabular_q_learning.environments.tunnels;

import domain_design_tabular_q_learning.domain.environment.helpers.GridInformerI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;

public record TunnelGridInformer(
        PropertiesTunnels ep
) implements GridInformerI<PropertiesRoad> {

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
