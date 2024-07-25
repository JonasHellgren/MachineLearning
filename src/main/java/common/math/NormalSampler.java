package common.math;

import java.util.Random;

public class NormalSampler {
        private final double mean;
        private final double stdDev;
        private final Random random;

        // Constructor to initialize mean and standard deviation
        public NormalSampler(double mean, double stdDev) {
            this.mean = mean;
            this.stdDev = stdDev;
            this.random = new Random();
        }

        // Method to generate a single sample
        public double generateSample() {
            return mean + stdDev * random.nextGaussian();
        }

}
