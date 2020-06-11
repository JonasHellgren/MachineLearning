package com.pluralsight.MazeNavigation.agent;

public class Status {
    private Pos2d s;  //present agent state
    private Pos2d sold;  //previous agent state
    private double R;  //reward of latest action

    public Status () {  //constructor
        Pos2d s=new Pos2d(1,1);
        Pos2d snew=new Pos2d(1,1);
        setS(s);  setSold(snew);
        R=0;
    }

    public Pos2d getS() {
        return s;
    }

    public void setS(Pos2d s) {
        this.s = s;
    }

    public Pos2d getSold() {
        return sold;
    }

    public void setSold(Pos2d sold) {
        this.sold = sold;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }


}
