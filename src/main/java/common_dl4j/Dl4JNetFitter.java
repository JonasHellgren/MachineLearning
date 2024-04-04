package common_dl4j;

import lombok.AllArgsConstructor;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
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
 * Collections.shuffle shuffles the entire list of DataSet objects before creating the ListDataSetIterator.
 * Each epoch will now process mini-batches that are randomly shuffled.
 * Nof batches per epoch is described in top of file.

 * Shuffling the entire dataset is a common and recommended practice in training neural networks,
 * shuffling within a mini-batch is less common and typically used in specific scenarios
 */

@AllArgsConstructor
public class Dl4JNetFitter {

    MultiLayerNetwork net;
    NetSettings netSettings;
    Random rnd;
    double lossLastFit;

    public Dl4JNetFitter(MultiLayerNetwork net, NetSettings netSettings) {
        this.net = net;
        this.netSettings = netSettings;
        this.rnd = new Random(netSettings.seed());
    }

    public void fit(INDArray in, INDArray out ) {
        int nPoints= (int) in.length();
        int sizeBatch=Math.min(netSettings.sizeBatch(),nPoints);
        int nFitsPerBatch= netSettings.nofFits(sizeBatch);
        DataSetIterator iterator = createDataSetIterator(in, out, sizeBatch);

        double sumLoss=0;
        while (iterator.hasNext()) {
            DataSet miniBatch = iterator.next();
            for (int i = 0; i < nFitsPerBatch; i++) {
                INDArray features = miniBatch.getFeatures();
                INDArray labels = miniBatch.getLabels();
                net.fit(features, labels); // Fit the model on each mini-batch
                sumLoss+=net.score();
               // miniBatch.shuffle();  //optional
            }
        }
        lossLastFit=sumLoss/(sizeBatch*nFitsPerBatch);
    }

    public double getLossLastFit() {
        return lossLastFit;
    }

    private DataSetIterator createDataSetIterator(INDArray in, INDArray out, int sizeBatch) {
        DataSet dataSet = new DataSet(in, out);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, rnd);
        return new ListDataSetIterator<>(listDs, sizeBatch);
    }
}
