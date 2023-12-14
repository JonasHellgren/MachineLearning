package common;

import org.apache.commons.math3.util.Pair;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dl4JUtil {

    /***
     *
     *
     * @param in:  List of in data points.
     *          in.add(List.of(1,2); in.add(List.of(2,2);  <=> inouts of 1st and second point
     * @param nofInputs  shall align with one sublist of the in argument
     * @return in converted to INDArray
     */

    public static INDArray convertListOfLists(List<List<Double>> in, int nofInputs) {
        int numRows = in.size();
        int numColumns = in.get(0).size();

        if (numColumns!= nofInputs) {
            throw new IllegalArgumentException("bad numColumns, numColumns = "+numColumns);
        }

        double[] flatArray = new double[numRows * numColumns];
        for (int i = 0; i < numRows; i++) {
            List<Double> row = in.get(i);
            for (int j = 0; j < numColumns; j++) {
                flatArray[i * numColumns + j] = row.get(j);
            }
        }
        return Nd4j.create(flatArray, new int[]{numRows, numColumns});
    }

    public static INDArray convertList(List<Double> in, int nofInputs) {
        return Nd4j.create(ListUtils.toArray(in),new int[]{1, nofInputs});
    }

    public static DataSetIterator getDataSetIterator(INDArray input, INDArray outPut, Random randGen) {
        DataSet dataSet = new DataSet(input, outPut);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, randGen);
        return new ListDataSetIterator<>(listDs, input.rows());
    }


    public static NormalizerMinMaxScaler createNormalizer(List<Pair<Double,Double>> inMinMax,
                                                          List<Pair<Double,Double>> outMinMax) {
    return  createNormalizer(inMinMax,outMinMax,Pair.create(0d,1d));

    }

        public static NormalizerMinMaxScaler createNormalizer(List<Pair<Double,Double>> inMinMax,
                                                           List<Pair<Double,Double>> outMinMax,
                                                              Pair<Double, Double> netMinMax) {
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(netMinMax.getFirst(),netMinMax.getSecond());
        List<Double> minInList=inMinMax.stream().map(p -> p.getFirst()).toList();
        List<Double> maxInList=inMinMax.stream().map(p -> p.getSecond()).toList();
        List<Double> minOutList=outMinMax.stream().map(p -> p.getFirst()).toList();
        List<Double> maxOutList=outMinMax.stream().map(p -> p.getSecond()).toList();
        normalizer.setFeatureStats(
                Nd4j.create(ListUtils.toArray(minInList)),
                Nd4j.create(ListUtils.toArray(maxInList)));
        normalizer.setLabelStats(
                Nd4j.create(ListUtils.toArray(minOutList)),
                Nd4j.create(ListUtils.toArray(maxOutList)));
        return normalizer;
    }


}
