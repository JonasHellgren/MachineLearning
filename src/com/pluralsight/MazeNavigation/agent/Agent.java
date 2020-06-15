package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Maze;

import java.util.Random;

public class Agent {  //This class represents an AI agent
    public Agentsetup setup;  //settings such as learning rate
    public Status status;  //variables as present position
    public TabularMemory memory;     //Action value function

    public Agent() {  //no arguments constructor
        this.setup = new Agentsetup();
        this.status = new Status();
        this.memory = new TabularMemory();
    }

    public void chooseAction() {  //This function chooses an action, can be random if prespribed by Pra
        Action ach;  //the chosen action
        double r = (Math.random() * 1);  //random number in [0,1]
        if (r < this.setup.getPra()) {  //random action?
            ach = Action.values()[new Random().nextInt(Action.values().length)];
        } else ach = getAopt(status.getS());  //non-random action in present state

        status.setAch(ach);
    }

    public void learnQ(Maze maze) {  //This method is used to for tabular Q-learning
        Pos2d s = status.getS();   //new state, after transition
        Pos2d sold = status.getSold();  //old state, before transition
        Action ach = status.getAch();  //chosen action
        Double Qsa = memory.readMem(sold, ach);  //Q value in state before transition
        Double Qsaopt;  //Q value in state after transition, assuming optimal action

        //following lines are not necessary but ensures zero Qsaopt is s is terminal
        if (maze.isStateTerminal(s))
            Qsaopt=0.0;
        else
            Qsaopt= memory.readMem(s, getAopt(s));

        //Q learning update
        Double Qsanew = Qsa + setup.alpha * (status.getR() + setup.gamma * Qsaopt - Qsa);
        memory.saveMem(sold, ach, Qsanew);
    }

    public Action getAopt(Pos2d s) { //This function calculates the optimal action in state s. Optimal means largest Qsa.
        Action aopt = null;  //the best action
        double Qbest = this.setup.Qverysmall;  //start value
        for (Action a : Action.values()) {
            double Qsa = memory.readMem(s, a);
            if (Qsa > Qbest) {  //better action found?
                Qbest = Qsa;
                aopt = a;
            }
        }
        return aopt;
    }

    public void clearMem()  { memory.clearMem(); }

    public double calcPra(int nepis, int nepismax) {  //This method calculates Pra for given nepis.
        //Pra is in  [Prastart,Praend]. Pra=Prastart if nepis is 0 and Pra=getPraend if nepis is nepismax.
       return setup.getPrastart() + (setup.getPraend() - setup.getPrastart()) * nepis / nepismax;
    }

}
