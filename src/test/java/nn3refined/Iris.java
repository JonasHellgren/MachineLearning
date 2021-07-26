package nn3refined;

import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.AsyncDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import static org.junit.Assert.*;

public class Iris {

    @Test
    public void testMultipleEpochsSimple(){
        //Mainly a simple sanity check on the preconditions in the method...
        DataSetIterator iter = new IrisDataSetIterator(10, 150);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .list()
                .layer(new OutputLayer.Builder().nIn(4).nOut(3).activation(Activation.SOFTMAX).build())
                .build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        net.fit(iter, 3);

        ComputationGraph g = net.toComputationGraph();
        g.fit(iter, 3);
    }

    @Test
    public void testInitializeNoNextIter() {
        DataSetIterator iter = new IrisDataSetIterator(10, 150);
        while (iter.hasNext()) iter.next();
        DataSetIterator async = new AsyncDataSetIterator(iter, 2);
        assertFalse(iter.hasNext());
        assertFalse(async.hasNext());
        try {
            iter.next();
            fail("Should have thrown NoSuchElementException");
        } catch (Exception e) {
            //OK
        }
        async.reset();
        int count = 0;
        while (async.hasNext()) {
            async.next();
            count++;
        }
        assertEquals(150 / 10, count);
    }

}
