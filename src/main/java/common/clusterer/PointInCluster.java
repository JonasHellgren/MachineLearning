package common.clusterer;

import lombok.ToString;
import org.apache.commons.math3.ml.clustering.Clusterable;

@ToString
public class PointInCluster implements Clusterable {
    private final double[] points;

    public PointInCluster(double[] points) {
        this.points = points;
    }

    @Override
    public double[] getPoint() {
        return points;
    }
}
