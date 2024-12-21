package book_rl_explained.radialbasis;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 *  large gammas <=>   RBF is highly localized
 */
public record KernelProperties(
        double[] centerCoordinates,
        double[] gammas) {

    public static KernelProperties ofGammas(double[] centerCoordinates, double[] gammas) {
        Preconditions.checkArgument(centerCoordinates.length == gammas.length,
                "centerCoordinates and gammas should have same length");
        return new KernelProperties(centerCoordinates, gammas);
    }

    public static KernelProperties ofSigmas(double[] centers, double[] sigmas) {
        Preconditions.checkArgument(centers.length == sigmas.length,
                "centers and sigmas must have same length");

        double[] gammas = IntStream.range(0, sigmas.length)
                .mapToDouble(i -> gamma(sigmas[i]))
                .toArray();
        return new KernelProperties(centers, gammas);

    }

    @Override
    public String toString() {
        return "KernelProperties{" +
                "centerCoordinates=" + Arrays.toString(centerCoordinates) +
                ", gammas=" + Arrays.toString(gammas) +
                '}';
    }


    /**
     * Sigma is like the radius of a circle, controlling the size of the kernel.
     * Gamma is like the "stickiness" of the kernel, controlling how quickly it
     * decays as you move away from the center.
     */

    public static double gamma(double sigma) {
        return 1 / (2 * sigma * sigma);
    }


}
