package dl4j_aux;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class TestMatrix {

    public static final double DELTA = 0.1;
    INDArray M;
    INDArray v;

    @Before
    public void init() {
        double[][] arr2Dim = {
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0},
                {7.0, 8.0, 9.0}};

        M = Nd4j.createFromArray(arr2Dim);

        double[] arr1Dim = {1.0, 2.0, 3.0};
        v = Nd4j.createFromArray(arr1Dim);

    }

    @Test
    public void testCreateMatrix() {
        System.out.println("M = " + M);
        System.out.println("v = " + v);

        int nofDimM = M.rank(); // The number of axes (dimensions) of the array.
        long[] sizeM = M.shape();  // The dimensions of the array. The size in each dimension.
        long nofElements = M.length();  // The total number of elements.

        Assert.assertEquals(2, nofDimM);
        Assert.assertEquals(3, sizeM[0]);
        Assert.assertEquals(3, sizeM[1]);
        Assert.assertEquals(3 * 3, nofElements);

    }

    @Test
    public void addMatrices() {
        INDArray sumM = M.add(M);

        System.out.println("sumM = " + sumM);

        Assert.assertEquals(1 + 1, sumM.getDouble(0, 0), DELTA);
        Assert.assertEquals(5 + 5, sumM.getDouble(1, 1), DELTA);
    }

    @Test
    public void addVectors() {
        INDArray sumV = v.add(v);

        System.out.println("sumM = " + sumV);

        Assert.assertEquals(1 + 1, sumV.getDouble(0), DELTA);
        Assert.assertEquals(3 + 3, sumV.getDouble(2), DELTA);
    }

    @Test
    public void subVectors() {
        INDArray sumV = v.sub(v);

        System.out.println("sumM = " + sumV);

        Assert.assertEquals(0, sumV.getDouble(0), DELTA);
        Assert.assertEquals(0, sumV.getDouble(2), DELTA);
    }

    @Test
    public void transposeMatrix() {
        INDArray Mtransp = M.transpose();
        System.out.println("Mtransp = " + Mtransp);
        Assert.assertEquals(4, Mtransp.getDouble(0, 1), DELTA);
    }

    @Test
    public void matrixVectorMultiplication() {
        INDArray resultV = M.mmul(v);

        System.out.println("resultV = " + resultV);
        int nofDimM = resultV.rank(); // The number of axes (dimensions) of the array.
        Assert.assertEquals(1, nofDimM);
        Assert.assertEquals(1 + 2 * 2 + 3 * 3, resultV.getDouble(0), DELTA);
    }

    @Test
    public void crossProduct() {

        double[] arr1Dim = {1.0, 0, 0};
        INDArray v1 = Nd4j.createFromArray(arr1Dim);
        double[] arr2Dim = {0.0, 1.0, 0.0};
        INDArray v2 = Nd4j.createFromArray(arr2Dim);

        INDArray resultV = vectorCross(v1, v2);
        System.out.println("resultV = " + resultV);

        int nofDimM = resultV.rank();
        Assert.assertEquals(1, nofDimM);
        Assert.assertEquals(0, resultV.getDouble(0), DELTA);
        Assert.assertEquals(1, resultV.getDouble(2), DELTA);

    }



    @Test
    public void invertMatrix() {
//        INDArray sumM = M.;

      //  System.out.println("sumM = " + sumM);

    //    Assert.assertEquals(1 + 1, sumM.getDouble(0, 0), DELTA);
     //   Assert.assertEquals(5 + 5, sumM.getDouble(1, 1), DELTA);
    }

    @Test
    public void normVector() {

        double norm=norm(v);
        System.out.println("norm = " + norm);

        Assert.assertEquals(Math.sqrt(1+4+9), norm, DELTA);
    }

    //https://en.wikipedia.org/wiki/Cross_product
    private INDArray vectorCross(INDArray a, INDArray b) {
        double a1 = a.getDouble(0);
        double a2 = a.getDouble(1);
        double a3 = a.getDouble(2);

        double[][] arr2Dim = {
                {0.0, -a3, a2},
                {a3, 0.0, -a1},
                {-a2, a1, 0.0}};

        M = Nd4j.createFromArray(arr2Dim);

        return M.mmul(b);

    }

    private double norm(INDArray v) {
        INDArray prod = v.mmul(v);
        return Math.sqrt(prod.getDouble(0));
    }

}
