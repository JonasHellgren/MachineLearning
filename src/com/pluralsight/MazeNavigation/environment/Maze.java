package com.pluralsight.MazeNavigation.environment;

import com.pluralsight.MazeNavigation.agent.Pos2d;

public class Maze {
    public static final byte minx=1;
    public static final byte miny=1;
    public static final byte maxx=4;
    public static final byte maxy=3;
    public static final double Pwd=0.2;

    public Maze () {}; //Constructor

    public boolean isStateFeasible(Pos2d s) {
       boolean inmaze=(minx <= s.getX() && s.getX() <= maxx) && (miny <= s.getY() && s.getY() <= maxy);
       boolean  atblockedcell=isStateBlocked(s);
       return inmaze && !atblockedcell;
    }

    public boolean isStateGoal(Pos2d s) {
        return (s.getX() ==4 && s.getY() == 3);
    }

    public boolean isStateTrap(Pos2d s) {
        return (s.getX() ==4 && s.getY() == 2);
    }

    public boolean isStateBlocked(Pos2d s) {
        return (s.getX() ==2 && s.getY() == 2);
    }

    public boolean isStateTerminal(Pos2d s) { return (isStateGoal(s) || isStateTrap(s));  }




}
