package book_rl_explained.radial_basis;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Represents a radial basis function (RBF) kernel.
 * A kernel is defined by its center coordinates and gammas (stickiness).
 */
public record Kernel  (
        double[] centerCoordinates,
        double[] gammas) {

    public static Kernel ofGammas(double[] centerCoordinates, double[] gammas) {
        Preconditions.checkArgument(centerCoordinates.length == gammas.length,
                "centerCoordinates and gammas should have same length");
        return new Kernel(centerCoordinates, gammas);
    }

    public static Kernel ofSigmas(double[] centers, double[] sigmas) {
        Preconditions.checkArgument(centers.length == sigmas.length,
                "centers and sigmas must have same length");

        double[] gammas = IntStream.range(0, sigmas.length)
                .mapToDouble(i -> gamma(sigmas[i]))
                .toArray();
        return new Kernel(centers, gammas);

    }

    /**
     * phi(x) = exp(-sum(gamma_i * (x_i - c_i)^2))
     */

    public double activation(double[] input) {
        double distanceSquaredSum = 0.0;
        for (int i = 0; i < input.length; i++) {
            distanceSquaredSum += gammas()[i]*Math.pow(input[i] - centerCoordinates()[i], 2);
        }
        return Math.exp(-distanceSquaredSum);
    }


    /**
     * Sigma is like the radius of a circle, controlling the size of the kernel.
     * Gamma is like the "stickiness" of the kernel, controlling how quickly it
     * decays as you move away from the center.
     */

    public static double gamma(double sigma) {
        return 1 / (2 * sigma * sigma);
    }

    public int nDimensions() {
        return centerCoordinates.length;
    }

    @Override
    public String toString() {
        return "Kernel{" +
                "centerCoordinates=" + Arrays.toString(centerCoordinates) +
                ", gammas=" + Arrays.toString(gammas) +
                '}';
    }


}
