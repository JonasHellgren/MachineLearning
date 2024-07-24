package maze_domain_design.domain.shared;

import maze_domain_design.environments.obstacle_on_road.PropertiesRoad;

public record GridSizeExtractor(
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
