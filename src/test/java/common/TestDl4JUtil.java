package common;

import common_dl4j.Dl4JUtil;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

public class TestDl4JUtil {


    @Test
    public void whenConvertListOfLists_thenCorrect() {

        List<List<Double>> listOfList = new ArrayList<>();
        listOfList.add(List.of(1d, 2d, 3d));
        listOfList.add(List.of(10d, 20d, 30d));

        INDArray indArray = Dl4JUtil.convertListOfLists(listOfList, 3);

        Assertions.assertEquals(2, indArray.size(0));
        Assertions.assertEquals(3, indArray.size(1));

        Assertions.assertEquals(1d, indArray.getDouble(0, 0));  //row, col
        Assertions.assertEquals(2d, indArray.getDouble(0, 1));
    }

    @Test
    public void whenConvertList_thenCorrect() {

        List<Double> list = List.of(1d, 2d, 3d);
        INDArray indArray = Dl4JUtil.convertListToOneRow(list, 3);
        Assertions.assertEquals(3, indArray.size(1));
        Assertions.assertEquals(1d, indArray.getDouble(0, 0));  //row, col
        Assertions.assertEquals(2d, indArray.getDouble(0, 1));
    }


    @Test
    public void whenCreateNormalizer_thenCorrect() {
        List<Pair<Double, Double>> minMaxList = new ArrayList<>();
        minMaxList.add(Pair.create(0d, 10d));   //min max of in 0
        minMaxList.add(Pair.create(0d, 100d));  //min max of in 1
        var normalizer = Dl4JUtil.createNormalizer(minMaxList, Pair.create(0d, 1d));
        Assertions.assertEquals(getIndArr(0d,0d),normalizer.getMin());
        Assertions.assertEquals(getIndArr(10d,100d),normalizer.getMax());

    }

    private static INDArray getIndArr(double v0, double v1) {
        return Nd4j.create(new double[]{v0, v1});
    }

}
