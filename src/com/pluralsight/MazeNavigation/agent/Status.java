package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;

public class Status {
    private Pos2d s;  //present agent state
    private Action ach;  //latest chosen action
    private double R;  //reward of latest action
    private Pos2d sold;  //previous agent state


    public Status () {  //constructor
        Pos2d s=new Pos2d(1,1);
        Pos2d sold=new Pos2d(1,1);
        setS(s);  setSold(sold);
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

    public Action getAch() {  return ach;  }

    public void setAch(Action ach) {    this.ach = ach;  }
}
