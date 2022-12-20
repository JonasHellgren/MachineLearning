package common;

import lombok.extern.java.Log;

import java.util.Arrays;

@Log
public final class Interpolator {

    double[] X;
    double[] Y;

    public Interpolator(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        this.X = x;
        this.Y = y;
    }

    public  double[] interpLinear(double[] xi) throws IllegalArgumentException {

        double[] dx = new double[X.length - 1];
        double[] dy = new double[X.length - 1];
        double[] slope = new double[X.length - 1];
        double[] intercept = new double[X.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < X.length - 1; i++) {
            dx[i] = X[i + 1] - X[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be monotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = Y[i + 1] - Y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = Y[i] - X[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > X[X.length - 1])) {
                yi[i] = Y[Y.length - 1];
                log.warning("Interpolating outside range (higher)");
            } else if ((xi[i] < X[0])) {
                yi[i] = Y[0];
                log.warning("Interpolating outside range (lower)");
            }
            else {
                int loc = Arrays.binarySearch(X, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                }
                else {
                    yi[i] = Y[loc];
                }
            }
        }

        return yi;
    }

}
