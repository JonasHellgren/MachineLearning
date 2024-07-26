package domain_design_tabular_q_learning.environments.shared;

import common.math.MathUtils;
import org.apache.commons.math3.util.Pair;

public record XyPos(Integer x, Integer y) {

    public static XyPos of(Integer x, Integer y) {
        return new XyPos(x,y);
    }

    public XyPos posClipped(Pair<Integer, Integer> minMaxX, Pair<Integer, Integer> minMaxY) {
        var xClipped = MathUtils.clip(x, minMaxX.getFirst(), minMaxX.getSecond());
        var yClipped = MathUtils.clip(y, minMaxY.getFirst(), minMaxY.getSecond());
        return XyPos.of(xClipped, yClipped);
    }

}
