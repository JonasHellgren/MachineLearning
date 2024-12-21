package book_rl_explained.radialbasisOld;

import book_rl_explained.radialbasis.KernelProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

 class TestKernelProperties {

    @Test
     void testOfGammas() {
        double[] centerCoordinates = {1.0, 2.0};
        double[] gammas = {3.0, 4.0};
        KernelProperties kernelProperties = KernelProperties.ofGammas(centerCoordinates, gammas);
        assertArrayEquals(centerCoordinates, kernelProperties.centerCoordinates());
        assertArrayEquals(gammas, kernelProperties.gammas());
    }

    @Test
     void testOfSigmas() {
        double[] centers = {1.0, 2.0};
        double[] sigmas = {3.0, 4.0};
        KernelProperties kernelProperties = KernelProperties.ofSigmas(centers, sigmas);
        assertArrayEquals(centers, kernelProperties.centerCoordinates());
        double[] expectedGammas = {1 / (2 * 3.0 * 3.0), 1 / (2 * 4.0 * 4.0)};
        assertArrayEquals(expectedGammas, kernelProperties.gammas(), 1e-6);
    }

    @Test
     void testGamma() {
        double sigma = 2.0;
        double expectedGamma = 1 / (2 * sigma * sigma);
        assertEquals(expectedGamma, KernelProperties.gamma(sigma), 1e-6);
    }

    @Test
     void testToString() {
        double[] centerCoordinates = {1.0, 2.0};
        double[] gammas = {3.0, 4.0};
        KernelProperties kernelProperties = KernelProperties.ofGammas(centerCoordinates, gammas);
        String expectedToString = "KernelProperties{centerCoordinates=[1.0, 2.0], gammas=[3.0, 4.0]}";
        assertEquals(expectedToString, kernelProperties.toString());
    }

    @Test
     void testInvalidCenterCoordinatesAndGammasLength() {
        double[] centerCoordinates = {1.0, 2.0};
        double[] gammas = {3.0};
        assertThrows(IllegalArgumentException.class, () -> KernelProperties.ofGammas(centerCoordinates, gammas));
    }

    @Test
     void testInvalidCentersAndSigmasLength() {
        double[] centers = {1.0, 2.0};
        double[] sigmas = {3.0};
        assertThrows(IllegalArgumentException.class, () -> KernelProperties.ofSigmas(centers, sigmas));
    }

}
