package common.clusterer;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to perform clustering on a set of points using the K-Means++ algorithm.
 */

public class KMeansClusterer {

    public static final int N_ITER_MAX = 100;
    public static final EuclideanDistance DISTANCE_MEASURE = new EuclideanDistance();
    List<PointInCluster> points;
    KMeansPlusPlusClusterer<PointInCluster> clusterCalculator;
    int nIterMax;
    int nDim;
    int nClusters;

    public KMeansClusterer(int nIterMax, int nDim, int nClusters, DistanceMeasure measure) {
        this.nIterMax = nIterMax;
        this.nDim = nDim;
        this.nClusters = nClusters;
        this.points = new ArrayList<>();
        this.clusterCalculator = new KMeansPlusPlusClusterer<>(nClusters, nIterMax, measure);
    }

    public static KMeansClusterer of(int nDim, int nClusters) {
        return new KMeansClusterer(N_ITER_MAX, nDim, nClusters, DISTANCE_MEASURE);
    }

    public void addPoint(double[] point) {
        Preconditions.checkArgument(point.length==nDim,"Wrong point dimension");
        points.add(new PointInCluster(point));
    }

    public void clear() {
        points.clear();
    }

    public int nPoints() {
        return points.size();
    }

    public List<CentroidCluster<PointInCluster>> getClusters() {
        Preconditions.checkArgument(nPoints()>0,"No points to analyze for clustering");
        return clusterCalculator.cluster(points);
    }


    void addPoint(PointInCluster point) {
        points.add(point);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        var clusters = getClusters();
        for (int i = 0; i < clusters.size(); i++) {
            sb.append("Cluster ").append(i + 1);
            for (PointInCluster point : clusters.get(i).getPoints()) {
                sb.append("\t").append(point).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }


}
