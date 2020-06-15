package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Maze;

import java.util.Random;

public class Agent {
    public Agentsetup setup;
    public Status status;
    public TabularMemory memory;

    public Agent() {  //no arguments constructor
        this.setup = new Agentsetup();
        this.status = new Status();
        this.memory = new TabularMemory();
    }

    public Agent(Agentsetup setup) {  //constructor
        this();  //call no arguments constructor
        this.setup = setup;  //will this work?
    }

    public void chooseAction() {
        Action ach;  //the chosen action
        double r = (Math.random() * 1);  //random number in [0,1]
        if (r < this.setup.getPra()) {  //random action?
            ach = Action.values()[new Random().nextInt(Action.values().length)];
        } else ach = getAopt(status.getS());  //non random action

        status.setAch(ach);
    }

    public void learnQ(Maze maze) {
        Pos2d s = status.getS();   //new state, after transition
        Pos2d sold = status.getSold();  //old state, before transition
        Action ach = status.getAch();
        Double Qsa = memory.readMem(sold, ach);
        Double Qsaopt;

        if (maze.isStateTerminal(s))
            Qsaopt=0.0;
        else
            Qsaopt= memory.readMem(s, getAopt(s));

        Double Qsanew = Qsa + setup.alpha * (status.getR() + setup.gamma * Qsaopt - Qsa);
        memory.saveMem(sold, ach, Qsanew);
    }

    public Action getAopt(Pos2d s) {
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

    public double calcPra(int nepis, int nepismax) {
       return setup.getPrastart() + (setup.getPraend() - setup.getPrastart()) * nepis / nepismax;
    }

}
