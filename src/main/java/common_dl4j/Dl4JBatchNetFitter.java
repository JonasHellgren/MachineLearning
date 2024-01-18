package common_dl4j;

import lombok.AllArgsConstructor;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.jetbrains.annotations.NotNull;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Following relations are essential:
 * sizeBatch=min(p.sizeBatch,nExper)
 * nBatch=nExper/sizeBatch, nFitsPerBatch=round(relNFits*sizeBatch)
 *
 * todo test using all points, not just sub set in batch
 */

@AllArgsConstructor
public class Dl4JBatchNetFitter {

    MultiLayerNetwork net;
    NetSettings netSettings;
    Random rnd;

    public Dl4JBatchNetFitter(MultiLayerNetwork net, NetSettings netSettings) {
        this.net = net;
        this.netSettings = netSettings;
        this.rnd = new Random(netSettings.seed());
    }

    public void batchFit(INDArray in, INDArray out ) {
        int nPoints= (int) in.length();
        int sizeBatch=Math.min(netSettings.sizeBatch(),nPoints);
        int nBatch= (int) Math.round(nPoints/(double) sizeBatch);
        int nFitsPerBatch= netSettings.nofFits(nBatch);

        DataSetIterator iterator = createDataSetIterator(in, out, sizeBatch);

   /*     net.setListeners(new ScoreIterationListener(1));
        System.out.println("nFitsPerBatch = " + nFitsPerBatch);
        System.out.println("before fitting");*/

        for (int i = 0; i < nFitsPerBatch; i++) {
            iterator.reset();
            net.fit(iterator);
        }
    }

    @NotNull
    private DataSetIterator createDataSetIterator(INDArray in, INDArray out, int sizeBatch) {
        DataSet dataSet = new DataSet(in, out);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, rnd);
        return new ListDataSetIterator<>(listDs, sizeBatch);
    }
}