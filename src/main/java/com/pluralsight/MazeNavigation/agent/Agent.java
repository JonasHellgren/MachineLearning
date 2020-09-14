package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Maze;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;
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

        //following lines are not necessary but ensures zero Qsaopt if s is terminal
        if (maze.isStateTerminal(s))
            Qsaopt=0.0;
        else
            Qsaopt= mem.readMem(s, getAopt(s,mem));

        //Q learning update
        Double Qsanew = Qsa + setup.alpha * (status.getR() + setup.gamma * Qsaopt - Qsa);
        tabmemory.saveMem(sold, ach, Qsanew);
    }

    public void learnNN(Maze maze) {  //This method is used to for tabular Q-learning

        List<Transition> rb = nnmemory.repBuff;  //reference to replay buffer
        List<Transition> mb = nnmemory.miniBatch;  //reference to mini batch

        //store transition in replay buffer
        Iterator<Transition> rbiter = rb.iterator();
        if (rb.size()>nnmemory.RBLEN)   //remove first/oldest item in set if set is "full"
            {rbiter.next(); rbiter.remove(); }
        Pos2d sold = new Pos2d(status.getSold());   Pos2d s = new Pos2d(status.getS());  //Important to use new
        Transition trans = new Transition(sold, status.getAch(), status.getR(), s);
        rb.add(trans);

        //counter is not zero => decrease counter and return else reset and continue
        if (!nnmemory.iszeroRbcount()) { nnmemory.decRbcount(); return;   }
        else
            nnmemory.resetRbcount();

        //set list rbKeys if rb is not of full length
        if (rb.size()<nnmemory.RBLEN) {
            nnmemory.rbKeys.clear();
            for (int i = 0; i < rb.size(); i++)
                nnmemory.rbKeys.add(i);
        }

        //create mini batch, i.e. sample random transitions from replay buffer
        //mini batch length restricted by min(rb present length, mb maxlen)
        Collections.shuffle(nnmemory.rbKeys);  //make number order random
        mb.clear();         int i = 0;
        while (i<rb.size() && i<nnmemory.MBLEN) {
            int chnr = (int) nnmemory.rbKeys.get(i);     mb.add(rb.get(chnr));
            i++;
        }

        //System.out.println("mb.size()"+mb.size());
        //for (Transition obj : mb) { System.out.println(obj);      }

        //set data points, i.e. calculate target q for each transition
        INDArray multinputs  = Nd4j.create(new double[nnmemory.MBLEN][NNMemory.INPUT_NEURONS]);
        INDArray multout   = Nd4j.create(new double[nnmemory.MBLEN][NNMemory.OUTPUT_NEURONS]);

        int rowi=0; Pos2d snext; double R, q, qopt;  Action a;
        for (Transition tr : mb) {
            s=tr.getS(); a=tr.getA(); R=tr.getR(); snext=tr.getSnext();

           if (maze.isStateTerminal(snext))
               q=R;
            else
            {   qopt = nnmemory.readMem(snext, getAopt(snext,nnmemory));
              q=1*(R+setup.getgamma()*qopt); }
            //q=R;

            multinputs.putRow(rowi, Nd4j.create(new double[] {s.getX(),s.getY(),a.val}));
            multout.putRow(rowi, Nd4j.create(new double[] {q}));
            rowi++;
        }

        //train NN
        DataSet ds = new DataSet(multinputs, multout);
        System.out.println(multinputs);  System.out.println(multout);
        for (int j = 0; j < nnmemory.NFITITERS; j++) {
            nnmemory.net.fit(ds);
        }

        multinputs.close();  multout.close();

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
