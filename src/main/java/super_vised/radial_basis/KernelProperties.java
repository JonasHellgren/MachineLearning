package super_vised.radial_basis;

import com.google.common.base.Preconditions;

import java.util.Arrays;

/**
 *  large gammas <=>   RBF is highly localized
 */
public record KernelProperties(
        double[] centerCoordinates,
        double[] gammas) {

    public static KernelProperties of(double[] centerCoordinates, double[] gammas) {
        Preconditions.checkArgument(centerCoordinates.length == gammas.length,
                "centerCoordinates and gammas should have same length");
        return new KernelProperties(centerCoordinates, gammas);
    }

    @Override
    public String toString() {
        return "KernelProperties{" +
                "centerCoordinates=" + Arrays.toString(centerCoordinates) +
                ", gammas=" + Arrays.toString(gammas) +
                '}';
    }

}
