package com.pluralsight.MazeNavigation.tests;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.NNMemory;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Status;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.generalfunctions.deepCopy;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class testDeepCopy {

    INDArray singleinput = Nd4j.zeros(1, NNMemory.INPUT_NEURONS);  //one row

    @Test
    public void simpleCopy() {
        Pos2d s = new Pos2d(1, 2);
        Pos2d snew = (Pos2d) deepCopy.Copy(s);
        //snew.setXY(3,4);
        System.out.println(s);
        System.out.println(snew);
        Assert.assertEquals(1, s.getX());

    }

    @Test
    public void nnSetParamsCopy() {
        Agent agent = new Agent();
        MultiLayerNetwork net1 = agent.nnmemory.net;
        MultiLayerNetwork net2 = agent.nnmemorytar.net;

        System.out.println(net1);
        System.out.println(net2);

        double[][] DoubleArrIn = new double[][]{{1.0, 1.0, 1.0}};  //x,y,a

        singleinput = Nd4j.create(DoubleArrIn);  //Create a row vector from a double array

        //define iterator
        INDArray outref2 = Nd4j.create(new double[1][NNMemory.OUTPUT_NEURONS]);
        outref2.putRow(0, Nd4j.create(new double[]{5}));
        final DataSet allData = new DataSet(singleinput, outref2);
        final List<DataSet> list = allData.asList();
        DataSetIterator iterator = new ListDataSetIterator<>(list, 1);
        //train NN
        for (int j = 0; j < 100; j++) {  //nof iter is NEPOCHS*RBLEN/MBLEN
            iterator.reset();
            net1.fit(iterator);
        }

        //now nets shall differ
        INDArray out1 = net1.output(singleinput);
        INDArray out2 = net2.output(singleinput);
        System.out.println(out1);
        System.out.println(out2);
        Assert.assertNotEquals(out1.getDouble(), out2.getDouble(), 0.01);

        //copy net2 params to net1
        net1.setParams(net2.params());

        //now net outputs shall be equal
        out1 = net1.output(singleinput);
        out2 = net2.output(singleinput);
        System.out.println(out1);
        System.out.println(out2);
        Assert.assertEquals(out1.getDouble(), out2.getDouble(), 0.01);


    }


}
