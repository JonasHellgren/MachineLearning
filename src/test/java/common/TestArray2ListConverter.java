package common;

import common.list_arrays.Array2ListConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestArray2ListConverter {

    @Test
    void whenList_thenCorrect() {
        double[] inArr=new double[]{0d,1d};
        List<Double> list= Array2ListConverter.convertDoubleArrToList(inArr);
        Assertions.assertEquals(0,list.get(0));
        Assertions.assertEquals(1,list.get(1));
    }


    @Test
    void whenListWithLists_thenCorrect() {
        double[][] inMat=new double[][]{{0d,1d},{2d,3d}};
        var listOfLists=Array2ListConverter.convertDoubleMatToListOfLists(inMat);
        Assertions.assertEquals(0,listOfLists.get(0).get(0));
        Assertions.assertEquals(3,listOfLists.get(1).get(1));
    }


}
