package com.pluralsight.MazeNavigation.agent;

public class Agent {
    public Agentsetup setup;
    public Status status;
    public TabularMemory memory;

    public Agent() {  //no arguments constructor
        this.setup=new Agentsetup();
        this.status = new Status();
        this.memory=new TabularMemory(); }

    public Agent(Agentsetup setup) {  //constructor
        this();  //call no arguments constructor
        this.setup = setup;  //will this work?
    }



}
