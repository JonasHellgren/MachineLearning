package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Maze;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import java.util.*;


public class Agent {  //This class represents an AI agent
    public Agentsetup setup;  //settings such as learning rate
    public Status status;  //variables as present position
    public TabularMemory tabmemory;     //Action value function
    public NNMemory nnmemory;  public NNMemory nnmemorytar;
    public List<Transition> repBuff;
    private int countNetRepl; //counter for how how often target net is replaced by policy net

    public Agent() {  //no arguments constructor
        this.setup = new Agentsetup();
        this.status = new Status();
        this.tabmemory = new TabularMemory();
        this.nnmemory = new NNMemory();
        this.nnmemorytar = new NNMemory();
        this.countNetRepl=setup.countNetReplMax;
        //create replay buffer
        this.repBuff=new ArrayList<>();  //ArrayList because get should be fast
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

    public void learnNN(Maze maze) {  //This method is used to for DQL

        List<Transition> rb = this.repBuff;  //reference to replay buffer

        //store transition in replay buffer
        Iterator<Transition> rbiter = rb.iterator();
        if (rb.size()>=nnmemory.RBLEN)   //remove first/oldest item in set if set is "full"
            {rbiter.next(); rbiter.remove(); }
        Pos2d sold = new Pos2d(status.getSold());   Pos2d s = new Pos2d(status.getS());  //Important to use new
        Transition trans = new Transition(sold, status.getAch(), status.getR(), s);
        rb.add(trans);

        //rb is not full => return
        if (rb.size()<nnmemory.RBLEN) { return;   }

        //set data points (inputs,out), i.e. calculate target q for each transition
        INDArray inputs  = Nd4j.create(new double[nnmemory.RBLEN][NNMemory.INPUT_NEURONS]);
        INDArray out   = Nd4j.create(new double[nnmemory.RBLEN][NNMemory.OUTPUT_NEURONS]);

        int rowi=0; Pos2d snext; double R, q, qopt;  Action a;
        for (Transition tr : rb) {    //loop replay buffer
            s=tr.getS(); a=tr.getA(); R=tr.getR(); snext=tr.getSnext();

            //input at rowi
            inputs.putRow(rowi, Nd4j.create(new double[] {s.getX(),s.getY()}));

           //define q, i.e. reference output for action a
           if (maze.isStateTerminal(snext))
               q=R;
            else
            { qopt = nnmemorytar.readMem(snext, getAopt(snext,nnmemorytar));
              q=1*(R+setup.getgamma()*qopt); }

            //define tempout, this output is similar as previous output except for output a
            INDArray oneinput= inputs.getRows(rowi);
            INDArray tempout = nnmemory.net.output(oneinput);
            tempout.putScalar(a.val,q);

            out.putRow(rowi, tempout);  //new out at rowi
            rowi++;
        }

        //define iterator
        int seed = 12345; Random rng = new Random(seed);
        final DataSet allData = new DataSet(inputs,out);
        final List<DataSet> list = allData.asList();
        Collections.shuffle(list,rng);
        DataSetIterator iterator = new ListDataSetIterator<>(list,nnmemory.MBLEN);

        //normalize, https://www.bookstack.cn/read/deeplearning4j/a7ea5ae658458207.md#Data%20Normalization
        //NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler();
        //normalizer.fit(iterator); iterator.setPreProcessor(normalizer);


        //train NN
        //DataSet ds = new DataSet(multinputs, multout);
        //System.out.println(inputs);  System.out.println(out);
        for (int j = 0; j < nnmemory.NEPOCHS; j++) {  //nof iter is NEPOCHS*RBLEN/MBLEN
            iterator.reset();   nnmemory.net.fit(iterator);
        }

        inputs.close();  out.close();  //clean up, maybe not necessary

        //every C step: copy default net params to target net
        if (countNetRepl==0) {
            nnmemorytar.net.setParams(nnmemory.net.params());
            countNetRepl=setup.countNetReplMax;
        }
        else
            countNetRepl--;
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
