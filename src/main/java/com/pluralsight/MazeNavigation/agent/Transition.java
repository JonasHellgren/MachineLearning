package com.pluralsight.MazeNavigation.agent;

import com.pluralsight.MazeNavigation.enums.Action;

public class Transition {
    Pos2d s;  //present state
    Action a; //used action
    double R; //reward
    Pos2d snext;  //next state

    public Transition(Pos2d s, Action a, double r, Pos2d snext) {
        this.s = s;  this.a = a;  R = r;   this.snext = snext;
    }

    public void setTrans(Pos2d s, Action a, double r, Pos2d snext) {
        this.s = s;  this.a = a;  R = r;   this.snext = snext;
    }

    public Pos2d getS() {    return s;   }
    public Action getA() {      return a;   }
    public double getR() {     return R;    }
    public Pos2d getSnext() {       return snext;    }

    @Override public String toString() {
        return "x:"+s.getX()+", y:"+s.getY()+", a:"+a.name()+", R:"+R+", xnext:"+snext.getX()+", ynext:"+snext.getY();
    }
}
