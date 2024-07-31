package safe_rl.persistance.trade_environment;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Preconditions;
import common.clusterer.KMeansClusterer;
import common.clusterer.PointInCluster;
import lombok.Builder;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Optional;

@Builder
public class ClosestPointToClusterCenterFinder {
    public static final int N_DIM = 2;

    List<Double> xValues;
    List<Double> yValues;
    int nClusters;

    public Pair<List<Double>, List<Double>> find() {
        Preconditions.checkArgument(!xValues.isEmpty() && xValues.size()==yValues.size(),
                "Bad format x/y values");
        var cluster = KMeansClusterer.of(N_DIM, nClusters);
        for (int i = 0; i < xValues.size(); i++) {
            cluster.addPoint(new double[]{xValues.get(i), yValues.get(i)});
        }
        var clusters = cluster.getClusters();
        List<Double> clustX = Lists.newArrayList();
        List<Double> clustY = Lists.newArrayList();
        for (CentroidCluster<PointInCluster> c : clusters) {
            double[] point = findClosestPointToCentroid(c).getPoint();
            clustX.add(point[0]);
            clustY.add(point[1]);
        }

        return Pair.create(clustX, clustY);
    }

    public static PointInCluster findClosestPointToCentroid(CentroidCluster<PointInCluster> cluster) {
        var points = cluster.getPoints();
        var centroid = cluster.getCenter();
        var distance = new EuclideanDistance();
        Optional<PointInCluster> closestPoint = Optional.empty();
        double minDistance = Double.MAX_VALUE;
        for (PointInCluster point : points) {
            double dist = distance.compute(point.getPoint(), centroid.getPoint());
            if (dist < minDistance) {
                minDistance = dist;
                closestPoint = Optional.of(point);
            }
        }
        return closestPoint.orElseThrow();
    }


}
