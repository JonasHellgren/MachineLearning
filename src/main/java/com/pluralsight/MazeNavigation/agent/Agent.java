package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Maze;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;

public class Agent {  //This class represents an AI agent
    public Agentsetup setup;  //settings such as learning rate
    public Status status;  //variables as present position
    public TabularMemory tabmemory;     //Action value function
    public NNMemory nnmemory;

    public Agent() {  //no arguments constructor
        this.setup = new Agentsetup();
        this.status = new Status();
        this.tabmemory = new TabularMemory();
        this.nnmemory = new NNMemory();
    }

    public void chooseAction(Memory mem) {  //This function chooses an action, can be random if prespribed by Pra
        Action ach;  //the chosen action
        double r = (Math.random() * 1);  //random number in [0,1]
        if (r < this.setup.getPra()) {  //random action?
            ach = Action.values()[new Random().nextInt(Action.values().length)];
        } else ach = getAopt(status.getS(),mem);  //non-random action in present state
        status.setAch(ach);
    }

    public void learnQ(Maze maze, Memory mem) {  //This method is used to for tabular Q-learning
        Pos2d s = status.getS();   //new state, after transition
        Pos2d sold = status.getSold();  //old state, before transition
        Action ach = status.getAch();  //chosen action
        Double Qsa = mem.readMem(sold, ach);  //Q value in state before transition
        Double Qsaopt;  //Q value in state after transition, assuming optimal action

        //following lines are not necessary but ensures zero Qsaopt is s is terminal
        if (maze.isStateTerminal(s))
            Qsaopt=0.0;
        else
            Qsaopt= mem.readMem(s, getAopt(s,mem));

        //Q learning update
        Double Qsanew = Qsa + setup.alpha * (status.getR() + setup.gamma * Qsaopt - Qsa);
        tabmemory.saveMem(sold, ach, Qsanew);
    }

    public void learnNN(Maze maze) {  //This method is used to for tabular Q-learning

        HashSet<Transition> rb = nnmemory.repBuff;  //reference to replay buffer
        HashSet<Transition> mb = nnmemory.miniBatch;  //reference to mini batch

        //store transition in replay buffer
        Iterator<Transition> rbiter = rb.iterator();
        if (rb.size()>nnmemory.RBLEN)   //remove first/oldest item in set if set is "full"
            {rbiter.next(); rbiter.remove(); }
        Pos2d sold = new Pos2d(status.getSold());   Pos2d s = new Pos2d(status.getS());  //Important to use new
        Transition trans = new Transition(sold, status.getAch(), status.getR(), s);
        rb.add(trans);

        System.out.println("Just added trans:"+trans);
        for (Transition obj : rb) { System.out.println(obj);      }


        //create mini batch, i.e. sample random transitions from replay buffer
        Collections.shuffle(nnmemory.rbKeys);  //make number order random
        mb.clear();
        //create randomIntsArray, copy of ArrayList numbers
        int[] randomIntsArray = new int[nnmemory.MBLEN];
        for (int i = 0; i < nnmemory.MBLEN; i++) { randomIntsArray[i] = (int) nnmemory.rbKeys.get(i);   }
        System.out.println("nnmemory.rbKeys"+nnmemory.rbKeys);
        //create mb, ArrayList with NELEMMB elements
        int i = 0;      int nadded = 0;
        for (Transition obj : rb) {
            int finalI = i;
            boolean contains = IntStream.of(randomIntsArray).anyMatch(x -> x == finalI);
            if (contains && nadded < nnmemory.MBLEN) {  mb.add(obj);     nadded++;      }
            i++;
        }

        System.out.println("mb.size()"+mb.size());
        for (Transition obj : mb) { System.out.println(obj);      }

        /*

        //set data points, i.e. calculate target q for each transition
        INDArray multinputs  = Nd4j.create(new double[nnmemory.MBLEN][NNMemory.INPUT_NEURONS]);
        INDArray multout   = Nd4j.create(new double[nnmemory.MBLEN][NNMemory.OUTPUT_NEURONS]);

        int rowi=0; Pos2d s,snext; double R, q, qopt;  Action a;
        for (Transition obj : mb) {
            s=obj.getS(); a=obj.getA(); R=obj.getR(); snext=obj.getSnext();

           // if (maze.isStateTerminal(s))
            //    q=R;
            //else
            //{   qopt = nnmemory.readMem(snext, getAopt(snext,nnmemory));
             //   q=R+setup.getgamma()*qopt; }
            q=R;

            multinputs.putRow(rowi, Nd4j.create(new double[] {s.getX(),s.getY(),a.val}));
            multout.putRow(rowi, Nd4j.create(new double[] {q}));
            rowi++;
        }

        //train NN
        DataSet ds = new DataSet(multinputs, multout);
        //System.out.println(multinputs);  System.out.println(multout);
        nnmemory.net.fit(ds);
        multinputs.close();  multout.close();
        */
    }


    public Action getAopt(Pos2d s, Memory mem) { //This function calculates the optimal action in state s. Optimal means largest Qsa.
        Action aopt = null;  //the best action
        double Qbest = this.setup.Qverysmall;  //start value
        for (Action a : Action.values()) {
            double Qsa = mem.readMem(s, a);
            if (Qsa > Qbest) {  //better action found?
                Qbest = Qsa;   aopt = a;
            }
        }
        return aopt;
    }

    public void clearMem()  { tabmemory.clearMem(); }

    public double calcPra(int nepis, int nepismax) {  //This method calculates Pra for given nepis.
        //Pra is in  [Prastart,Praend]. Pra=Prastart if nepis is 0 and Pra=getPraend if nepis is nepismax.
       return setup.getPrastart() + (setup.getPraend() - setup.getPrastart()) * nepis / nepismax;
    }

}
