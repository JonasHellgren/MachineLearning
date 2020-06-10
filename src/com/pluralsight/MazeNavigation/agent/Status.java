package com.pluralsight.MazeNavigation.agent;

public class Status {
    private State s;  //present agent state
    private State sold;  //old agent state

    Status () {  //constructor
        State s=new State(1,1);
        State snew=new State(1,1);
        setS(s);  setSold(snew);
    }

    public State getS() {
        return s;
    }

    public void setS(State s) {
        this.s = s;
    }

    public State getSold() {
        return sold;
    }

    public void setSold(State sold) {
        this.sold = sold;
    }
}
