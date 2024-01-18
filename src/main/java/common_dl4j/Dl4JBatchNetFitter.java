package common_dl4j;

import lombok.AllArgsConstructor;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class Dl4JBatchNetFitter {

    MultiLayerNetwork net;
    NetSettings netSettings;

    public void batchFit(INDArray in,  INDArray out ) {
        int nPoints= (int) in.length();
        DataSet dataSet = new DataSet(in, out);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, new Random(netSettings.seed()));
        int sizeBatch=Math.min(netSettings.sizeBatch(),nPoints);
        int nBatch= (int) Math.round(nPoints/(double) sizeBatch);
        int nFitsPerBatch= netSettings.nofFits(nBatch);
        DataSetIterator iterator = new ListDataSetIterator<>(listDs, sizeBatch);
   /*     net.setListeners(new ScoreIterationListener(1));
        System.out.println("nFitsPerBatch = " + nFitsPerBatch);
        System.out.println("before fitting");*/
        for (int i = 0; i < nFitsPerBatch; i++) {
            iterator.reset();
            net.fit(iterator);
        }


    }
}
