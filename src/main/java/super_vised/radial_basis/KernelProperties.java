package super_vised.radial_basis;

import java.util.Arrays;

/**
 *  large gamma <=>   RBF is highly localized
 */
public record KernelProperties(
        double[] centerCoordinates,
        double gamma) {

    public static KernelProperties of(double[] centerCoordinates, double gamma) {
        return new KernelProperties(centerCoordinates, gamma);
    }

    @Override
    public String toString() {
        return "KernelProperties{" +
                "centerCoordinates=" + Arrays.toString(centerCoordinates) +
                ", gamma=" + gamma +
                '}';
    }

}
