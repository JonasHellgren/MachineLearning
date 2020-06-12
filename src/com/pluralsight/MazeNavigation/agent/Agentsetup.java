package com.pluralsight.MazeNavigation.agent;

public class Agentsetup {
    //protected <=> this class, package and subclasses has access
    protected final double alpha=0.1;
    protected final double gamma=0.999;
    protected final double Prastart=0.5;
    protected final double Praend=0.01;
    protected double Pra=0.2;
    protected final double Qverysmall=-1000000;

    public Agentsetup() {    }  //constructor

    public double getPra() {
        return Pra;
    }

    public void setPra(double pra) {
        Pra = pra;
    }

    public double getPrastart() {
        return Prastart;
    }

    public double getPraend() {
        return Praend;
    }
}
