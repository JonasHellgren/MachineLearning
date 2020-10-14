package com.pluralsight.MazeNavigation.tests;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.NNMemory;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Transition;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.enums.MemType;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.hmi.HMI;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.text.DecimalFormat;
import java.util.Random;

import java.util.*;



public class TestNNMem {

    Agent agent = new Agent();
    MultiLayerNetwork net = agent.nnmemory.net;
    INDArray singleinput = Nd4j.zeros(1, NNMemory.INPUT_NEURONS);  //one row
    INDArray singleout = Nd4j.zeros(1, NNMemory.OUTPUT_NEURONS);  //one row
    final int ITERATIONS = 2000;
    static final int NELEM = 10;
    final int NELEMMB = 3;
    static int listenerFrequency=500;
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
        double[][] myDoubleArr = new double[][]{{1.0, 1.0}};
        singleinput = Nd4j.create(myDoubleArr);  //Create a row vector from a double array
        INDArray out = net.output(singleinput);
        System.out.println(out);
        Assert.assertEquals(1, out.rows());  //Returns the number of rows
    }

    @Test
    public void CalcMultipleNNOut() {

        INDArray oneinput  = Nd4j.create(new double[1][2]);
        for (int x = 1; x <4 ; x++) {
            for (int y = 0; y < 4; y++) {
                oneinput.putRow(0, Nd4j.create(new double[]{x, y}));  //x,y
                INDArray out = net.output(oneinput);
                System.out.print(oneinput+":");              System.out.println(out);
            }
        }

    }

    @Test
    public void TrainAt111() {

        INDArray indata  = Nd4j.create(new double[1][NNMemory.INPUT_NEURONS]);
        indata.putRow(0, Nd4j.create(new double[] {3,3}));
        INDArray outdata   = Nd4j.create(new double[1][NNMemory.OUTPUT_NEURONS]);
        outdata.putRow(0, Nd4j.create(new double[] {-0.5,0.1,0.5,1}));

        DataSet ds = new DataSet(indata, outdata);


        net.setListeners(new ScoreIterationListener(10));
        for (int i = 0; i < ITERATIONS; i++) {      net.fit(ds);    }

        System.out.println(indata);
        INDArray out = net.output(indata);
        System.out.println(out);
        //Assert.assertEquals(DESOUT, out.getDouble(0, 0), 0.1);  //(float expected, float actual, float delta)
    }


    @Test
    public void TrainAt000and111and222() {

        INDArray multinputs  = Nd4j.create(new double[3][NNMemory.INPUT_NEURONS]);
        multinputs.putRow(0, Nd4j.create(new double[] {3,1}));
        multinputs.putRow(1, Nd4j.create(new double[] {3,2}));
        multinputs.putRow(2, Nd4j.create(new double[] {3,3}));

        INDArray multout   = Nd4j.create(new double[3][NNMemory.OUTPUT_NEURONS]);
        multout.putRow(0, Nd4j.create(new double[] {-0.5,0.1,0.5,1}));
        multout.putRow(1, Nd4j.create(new double[] {0.5,-0.6,0.5,0}));
        multout.putRow(2, Nd4j.create(new double[] {0.1,0.4,-0.5,1}));

        DataSet ds = new DataSet(multinputs, multout);

        net.setListeners(new ScoreIterationListener(10));
        for (int i = 0; i < ITERATIONS; i++) {   net.fit(ds);    }

        System.out.println(multinputs);
        INDArray out = net.output(multinputs);        System.out.println(out);

    }

    @Ignore
    @Test
    public void CreaterepBuff() {
        final int NELEM = 10;
        List<Transition> rb = agent.repBuff;
        SetrepBuff(NELEM, rb);  //add elements to rb
        Assert.assertEquals(NELEM, rb.size());  //(long expected, long actual)
        for (Transition obj : rb) { System.out.println(obj);      }
        rb.clear();
    }

    @Ignore
    @Test
    public void TestShowMem() {
        HMI.showMem(agent, env.maze, Showtype.BESTA, MemType.NN);
        HMI.showMem(agent, env.maze, Showtype.MAXQ, MemType.NN);

    }


    void SetrepBuff(int nofelem, List<Transition> rb) {
        //Pos2d s = new Pos2d(1, 1);
        Random rand= new Random();;
        int min=0; int max=4;
        Action a = Action.N;

        for (int i = 0; i < nofelem; i++) {
            //double R = (Math.random() * 1);
            int r2=rand.nextInt((max - min) + 1) + min;
            Pos2d s = new Pos2d(1,r2);  //s.setXY(r1,r2);
            Pos2d snew = new Pos2d(1+1,r2+1);

            double R=s.getX()+s.getY()*0.1;
            Transition trans = new Transition(s, a, R, snew);
            //System.out.println(trans);
            rb.add(trans);
        }
    }




    @Test
    public void learnNNAtX3Y3() {
        Environment env = new Environment.Builder()
                .defPwd(0.0).build();
        int nepismax=100; int nepis=0;
        //Pos2d s=new Pos2d(1,1);
        Pos2d s=agent.status.getS();   //refers to state in agent status
        //Pos2d sold=agent.status.getSold();   //refers to state in agent status

        net.setListeners(new ScoreIterationListener(listenerFrequency));
        agent.setup.setgamma(0.5);

        do {
            //System.out.println("nepis:"+nepis);
            s.setXY(3,2);
            //while (!env.maze.isStateTerminal(s))   {
                agent.setup.setPra(agent.setup.getPrastart()+(agent.setup.getPraend()-agent.setup.getPrastart())*nepis/nepismax);
                agent.chooseAction(agent.tabmemory);   //action selection from present policy
                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R

                //System.out.print(agent.status.getAch()+", "); System.out.println(agent.status.getS());
                agent.learnQ(env.maze,agent.tabmemory);        //updating memory from experience
                agent.learnNN(env.maze);        //updating memory from experience
            //}

            if (agent.repBuff.size()>=agent.nnmemory.RBLEN) { nepis++;}


        } while (nepis<nepismax);

        Pos2d sold=agent.status.getSold();   //refers to state in agent status
        System.out.println("tabQsoldN:"+agent.tabmemory.readMem(sold,Action.N));
        System.out.println("tabQsoldE:"+agent.tabmemory.readMem(sold,Action.E));
        System.out.println("tabQsoldS:"+agent.tabmemory.readMem(sold,Action.S));
        System.out.println("tabQsoldW:"+agent.tabmemory.readMem(sold,Action.W));

        System.out.println("QsoldN:"+agent.nnmemory.readMem(sold,Action.N));
        System.out.println("QsoldE:"+agent.nnmemory.readMem(sold,Action.E));
        System.out.println("QsoldS:"+agent.nnmemory.readMem(sold,Action.S));
        System.out.println("QsoldW:"+agent.nnmemory.readMem(sold,Action.W));

        Assert.assertEquals(-1.04,agent.nnmemory.readMem(sold, Action.E),0.05);

        agent.clearMem();  agent.setup.setgamma(1);
    }

    @Test
    public void learnNNFromTrials() {
        Environment env = new Environment.Builder()
                .defPwd(0.2).build();
        agent.setup.setgamma(1);
        //Pos2d s=new Pos2d(1,1);
        Pos2d s=agent.status.getS();   //refers to state in agent status
        //Pos2d sold=agent.status.getSold();   //refers to state in agent status
        //Random rand= new Random(); int max=3;   int min=3;
        final boolean policyfromnn=true;

        net.setListeners(new ScoreIterationListener(listenerFrequency));

        //following gives connection problems
        //UIServer uiServer = UIServer.getInstance();
        //StatsStorage statsStorage = new InMemoryStatsStorage();
        //uiServer.attach(statsStorage);
        //net.addListeners(new StatsListener(statsStorage, listenerFrequency));

        int nstepsmax = 1000;
        int nsteps = 0; //number of steps
        do {
            //System.out.println("nepis:"+nepis);
            int x = 1;  int y = 1;  s.setXY(x, y);   //set start state, always lower-left cell
            while (!env.maze.isStateTerminal(s) && nsteps < nstepsmax) {
                agent.setup.setPra(agent.setup.getPrastart() + (agent.setup.getPraend() - agent.setup.getPrastart()) * nsteps / nstepsmax);
                if (policyfromnn)
                    agent.chooseAction(agent.nnmemory);   //action selection from nn memory
                else
                    agent.chooseAction(agent.tabmemory);   //action selection from tab memory

                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
                agent.learnQ(env.maze, agent.tabmemory);        //updating memory from experience
                agent.learnNN(env.maze);        //updating memory from experience
                if (agent.repBuff.size() >= agent.nnmemory.RBLEN) {
                    nsteps++;
                }

            }
        } while (nsteps<nstepsmax);

        System.out.println("Tab memory");
        for (int y = 3; y >= 1; y--)
            for (int x = 1; x <= 3; x++) {
                s.setXY(x, y);
                System.out.print("x:"+x+",y:"+y+"___");
                printMemTab(s);  //show mem at s
            }


        HMI.showMem(agent, env.maze, Showtype.MAXQ, MemType.Tab);
        System.out.println();
        HMI.showMem(agent, env.maze, Showtype.BESTA, MemType.Tab);

        System.out.println("NN memory");
        for (int y = 3; y >= 1; y--)
            for (int x = 1; x <= 3; x++) {
                s.setXY(x, y);
                System.out.print("x:"+x+",y:"+y+"___");
                printMemNN(s);  //show mem at s
            }

        HMI.showMem(agent, env.maze, Showtype.MAXQ, MemType.NN);
        System.out.println();
        HMI.showMem(agent, env.maze, Showtype.BESTA, MemType.NN);


        s.setXY(3, 3);
        Assert.assertEquals(0.95,agent.nnmemory.readMem(s, Action.E),0.05);


        agent.clearMem();  agent.setup.setgamma(1);
    }

    public void printMemTab(Pos2d s) {
        DecimalFormat df = new DecimalFormat(); df.setMaximumFractionDigits(2);
        System.out.print("QN:" + df.format(agent.tabmemory.readMem(s, Action.N)));
        System.out.print(", QE:" + df.format(agent.tabmemory.readMem(s, Action.E)));
        System.out.print(", QS:" + df.format(agent.tabmemory.readMem(s, Action.S)));
        System.out.println(", QW:" + df.format(agent.tabmemory.readMem(s, Action.W)));
    }

    public void printMemNN(Pos2d s) {
        DecimalFormat df = new DecimalFormat(); df.setMaximumFractionDigits(2);
        System.out.print("QN:"+df.format(agent.nnmemory.readMem(s,Action.N)));
        System.out.print(", QE:"+df.format(agent.nnmemory.readMem(s,Action.E)));
        System.out.print(", QS:"+df.format(agent.nnmemory.readMem(s,Action.S)));
        System.out.println(", QW:"+df.format(agent.nnmemory.readMem(s,Action.W)));
    }


}
