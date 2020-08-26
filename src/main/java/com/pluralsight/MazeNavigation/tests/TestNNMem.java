package com.pluralsight.MazeNavigation.tests;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.NNMemory;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Transition;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.enums.MemType;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.environment.Maze;
import com.pluralsight.MazeNavigation.hmi.HMI;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;

import java.util.*;
import java.util.stream.IntStream;






public class TestNNMem {

    Agent agent = new Agent();
    MultiLayerNetwork net = agent.nnmemory.net;
    INDArray singleinput = Nd4j.zeros(1, NNMemory.INPUT_NEURONS);  //one row
    INDArray singleout = Nd4j.zeros(1, NNMemory.OUTPUT_NEURONS);  //one row
    final int ITERATIONS = 500;
    static final int NELEM = 10;
    final int NELEMMB = 3;
    Environment env = new Environment.Builder()
            .defPwd(0.2).build();
    //INDArray multinputs = Nd4j.zeros(NELEMMB, NNMemory.INPUT_NEURONS);  //NELEMMB rows
    //INDArray multout = Nd4j.zeros(NELEMMB, NNMemory.OUTPUT_NEURONS);  //NELEMMB rows

    // define numbers, generate NELEMMB unique random number between 0 to NELEMMB
    static ArrayList numbers = new ArrayList();

    @BeforeClass
    static public void setNumbers() { for (int i = 0; i < NELEM; i++) numbers.add(i); }


    @Test
    public void CalcNNOut() {
        double[][] myDoubleArr = new double[][]{{1.0, 1.0, 1.0}};
        singleinput = Nd4j.create(myDoubleArr);  //Create a row vector from a double array
        INDArray out = net.output(singleinput);
        System.out.println(out);
        Assert.assertEquals(1, out.rows());  //(long expected, long actual)
    }

    @Test
    public void CalcMultipleNNOut() {

        INDArray oneinput  = Nd4j.create(new double[1][3]);
        for (int x = 1; x <4 ; x++) {
            for (int an = 0; an < 4; an++) {
                oneinput.putRow(0, Nd4j.create(new double[]{x, 1.0, (double) an / 1}));  //x,y,a
                INDArray out = net.output(oneinput);
                System.out.print(oneinput+":");              System.out.println(out);
            }
        }

    }

    @Test
    public void TrainAt111() {
        final double DESOUT = 0.5;
        double[][] DoubleArrIn = new double[][]{{1.0, 1.0, 1.0}};  //x,y,a
        singleinput = Nd4j.create(DoubleArrIn);  //Create a row vector from a double array
        double[][] DoubleArrOut = new double[][]{{DESOUT}};
        singleout = Nd4j.create(DoubleArrOut);  //Create a row vector from a double array
        DataSet ds = new DataSet(singleinput, singleout);

        System.out.println(singleinput);
        net.setListeners(new ScoreIterationListener(10));
        for (int i = 0; i < ITERATIONS; i++) {      net.fit(ds);    }

        INDArray out = net.output(singleinput);
        System.out.println(out);
        Assert.assertEquals(DESOUT, out.getDouble(0, 0), 0.1);  //(float expected, float actual, float delta)
    }

    @Test
    public void TrainAt000and111and222() {
        final double DESOUT0 = 0.0; final double DESOUT1 = 1; final double DESOUT2 = 1;
        INDArray multinputs  = Nd4j.create(new double[NELEMMB][3]);
        multinputs.putRow(0, Nd4j.create(new double[] {0,0,0}));
        multinputs.putRow(1, Nd4j.create(new double[] {1,1,1}));
        multinputs.putRow(2, Nd4j.create(new double[] {2,2,2}));

        INDArray multout   = Nd4j.create(new double[NELEMMB][1]);
        multout.putRow(0, Nd4j.create(new double[] {DESOUT0}));
        multout.putRow(1, Nd4j.create(new double[] {DESOUT1}));
        multout.putRow(2, Nd4j.create(new double[] {DESOUT2}));

        DataSet ds = new DataSet(multinputs, multout);

        net.setListeners(new ScoreIterationListener(10));
        for (int i = 0; i < ITERATIONS; i++) {   net.fit(ds);    }

        System.out.println(multinputs);
        INDArray oneinput  = Nd4j.create(new double[1][3]);
        oneinput.putRow(0,multinputs.getRow(0));
        System.out.println(oneinput);
        INDArray out = net.output(oneinput);        System.out.println(out);
        Assert.assertEquals(DESOUT0, out.getDouble(0, 0), 0.1);  //(float expected, float actual, float delta)

        oneinput.putRow(0,multinputs.getRow(1)); out = net.output(oneinput);
        System.out.println(oneinput);      System.out.println(out);
        Assert.assertEquals(DESOUT1, out.getDouble(0, 0), 0.1);  //(float expected, float actual, float delta)
    }


    @Test
    public void CreaterepBuff() {
        final int NELEM = 10;
        HashSet<Transition> rb = agent.nnmemory.repBuff;
        SetrepBuff(NELEM, rb);  //add elements to rb
        Assert.assertEquals(NELEM, rb.size());  //(long expected, long actual)
        for (Transition obj : rb) { System.out.println(obj);      }
        rb.clear();
    }

    @Test
    public void GetMiniBatch() {

        HashSet<Transition> rb = agent.nnmemory.repBuff;
        rb.clear();
        SetrepBuff(NELEM, rb);  //add elements to rb

        HashSet<Transition> mb = new HashSet<>();
        SetMiniBatch(rb, mb);
        System.out.println(numbers);
        for (Transition obj : mb) { System.out.println(obj);      }

        rb.clear();        mb.clear();
    }


    @Test
    public void TestTrainNN() {

        HashSet<Transition> rb = agent.nnmemory.repBuff;
        rb.clear();
        SetrepBuff(NELEM, rb);  //add elements to rb

        HashSet<Transition> mb = new HashSet<>();
        SetMiniBatch(rb, mb);

        INDArray multinputs  = Nd4j.create(new double[NELEMMB][NNMemory.INPUT_NEURONS]);
        INDArray multout   = Nd4j.create(new double[NELEMMB][NNMemory.OUTPUT_NEURONS]);
        double q,R; Action a;

        int rowi=0; Pos2d s,snext;
        for (Transition obj : mb) {
        R=obj.getR(); s=obj.getS(); snext=obj.getSnext(); a=obj.getA();
        q=R; //TODO
        multinputs.putRow(rowi, Nd4j.create(new double[] {s.getX(),s.getY(),a.val}));
        multout.putRow(rowi, Nd4j.create(new double[] {q}));
        rowi++;
        }

        DataSet ds = new DataSet(multinputs, multout);
        System.out.println(multinputs);  System.out.println(multout);
        net.fit(ds);
        rb.clear();        mb.clear(); multinputs.close();  multout.close();
    }

    @Test
    public void TestcalcNewq() {

        Pos2d s = new Pos2d(1,1);  //s.setXY(r1,r2);
        Pos2d snext = new Pos2d(1, 2);
        double R=s.getX()+s.getY()*0.1;  double Qsa = 0; double Qbest=-1000;
        for (Action a : Action.values()) {
            Qsa = agent.nnmemory.readMem(snext, a);
            System.out.println("Action:"+a+", Qsa:"+Qsa);
            if (Qsa>Qbest) { Qbest=Qsa; }
        }

        double q=calcNewq(R,snext);
        System.out.println("q:"+q);
        Assert.assertEquals(R+agent.setup.getgamma()*Qbest,q, 0.01);
    }

    @Test
    public void TestShowMem() {
        HMI.showMem(agent, env.maze, Showtype.BESTA, MemType.NN);
        HMI.showMem(agent, env.maze, Showtype.MAXQ, MemType.NN);

    }


    void SetrepBuff(int nofelem, HashSet<Transition> rb) {
        //Pos2d s = new Pos2d(1, 1);
        Random rand= new Random();;
        int min=0; int max=4;
        Action a = Action.N;

        for (int i = 0; i < nofelem; i++) {
            //double R = (Math.random() * 1);
            int r1=rand.nextInt((max - min) + 1) + min;
            int r2=rand.nextInt((max - min) + 1) + min;
            Pos2d s = new Pos2d(r1,r2);  //s.setXY(r1,r2);
            Pos2d snew = new Pos2d(r1+1,r2+1);

            double R=s.getX()+s.getY()*0.1;
            Transition trans = new Transition(s, a, R, snew);
            //System.out.println(trans);
            rb.add(trans);
        }
    }

    void SetMiniBatch(HashSet<Transition> rb, HashSet<Transition> mb) {

        Collections.shuffle(numbers);
        //create randomIntsArray, copy of ArrayList numbers
        int[] randomIntsArray = new int[NELEMMB];
        for (int i = 0; i < NELEMMB; i++) { randomIntsArray[i] = (int) numbers.get(i);   }
        //create mb, ArrayList with NELEMMB elements
        int i = 0;      int nadded = 0;
        for (Transition obj : rb) {
            int finalI = i;
            boolean contains = IntStream.of(randomIntsArray).anyMatch(x -> x == finalI);
            if (contains && nadded < NELEMMB) {  mb.add(obj);     nadded++;      }
            i++;
        }
    }

    double calcNewq(double R,Pos2d snext) {
        double q;
        if (Maze.isStateTerminal(snext))
            q=R;
        else {
            double Qsaopt = agent.nnmemory.readMem(snext, agent.getAopt(snext,agent.nnmemory));
            q=R+agent.setup.getgamma()*Qsaopt;
        }
        return q;
    }

    @Test
    public void learnNNAtX3Y3() {
        Environment env = new Environment.Builder()
                .defPwd(0.0).build();
        int nepismax=3; int nepis=0;
        //Pos2d s=new Pos2d(1,1);
        Pos2d s=agent.status.getS();   //refers to state in agent status
        //Pos2d sold=agent.status.getSold();   //refers to state in agent status


        do {
            System.out.println("nepis:"+nepis);
            s.setXY(3,3);
            //while (!env.maze.isStateTerminal(s))   {
                agent.setup.setPra(agent.setup.getPrastart()+(agent.setup.getPraend()-agent.setup.getPrastart())*nepis/nepismax);
                agent.chooseAction(agent.tabmemory);   //action selection from present policy
                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R

                System.out.print(agent.status.getAch()+", "); System.out.println(agent.status.getS());
                agent.learnQ(env.maze,agent.tabmemory);        //updating memory from experience
                agent.learnNN(env.maze);        //updating memory from experience
            //}

            nepis++;
        } while (nepis<nepismax);

        Pos2d sold=agent.status.getSold();   //refers to state in agent status
        System.out.println("QsoldN:"+agent.tabmemory.readMem(sold,Action.N));
        System.out.println("QsoldE:"+agent.tabmemory.readMem(sold,Action.E));
        System.out.println("QsoldS:"+agent.tabmemory.readMem(sold,Action.S));
        System.out.println("QsoldW:"+agent.tabmemory.readMem(sold,Action.W));

        System.out.println("QsoldN:"+agent.nnmemory.readMem(sold,Action.N));
        System.out.println("QsoldE:"+agent.nnmemory.readMem(sold,Action.E));
        System.out.println("QsoldS:"+agent.nnmemory.readMem(sold,Action.S));
        System.out.println("QsoldW:"+agent.nnmemory.readMem(sold,Action.W));

        Assert.assertEquals(1,agent.nnmemory.readMem(sold, Action.E),0.05);

        agent.clearMem();
    }

}
