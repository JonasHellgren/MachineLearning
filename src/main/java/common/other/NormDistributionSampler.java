package common.other;

import org.apache.commons.math3.util.Pair;

import java.util.Random;

/**
 * https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
 */
public class NormDistributionSampler {

    Random random=new Random();

    public double sampleFromNormDistribution(Pair<Double, Double> meanStdPair) {
        return sampleFromNormDistribution(meanStdPair.getFirst(), meanStdPair.getSecond());
    }


    public double sampleFromNormDistribution(double mean, double std) {
        return mean+random.nextGaussian() * std;
    }

}
