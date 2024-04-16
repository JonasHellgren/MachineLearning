package common;

import common.list_arrays.List2ArrayConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class TestList2ArrayConverter {


    @Test
    void whenList_thenCorrect() {
        List<Double> inList=List.of(0d,1d);
        double [] arrVec= List2ArrayConverter.convertListToDoubleArr(inList);
        Assertions.assertEquals(0,arrVec[0]);
        Assertions.assertEquals(1,arrVec[1]);
    }


    @Test
    void whenListWithLists_thenCorrect() {
        List<List<Double>> inList=List.of(List.of(0d,1d),List.of(2d,3d));
        double [][] arrMat=List2ArrayConverter.convertListWithListToDoubleMat(inList);

        System.out.println("arrMat = " + Arrays.deepToString(arrMat));

        Assertions.assertEquals(0,arrMat[0][0]);
        Assertions.assertEquals(3,arrMat[1][1]);

    }


}
