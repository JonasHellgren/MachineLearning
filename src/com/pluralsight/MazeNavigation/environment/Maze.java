package com.pluralsight.MazeNavigation.environment;

import com.pluralsight.MazeNavigation.agent.State;

public class Maze {
    public static final byte minx=1;
    public static final byte miny=1;
    public static final byte maxx=4;
    public static final byte maxy=3;

    public Maze () {}; //Constructor

    public boolean isStateFeasible(State s) {
       boolean inmaze=(minx <= s.x && s.x <= maxx) && (miny <= s.y && s.y <= maxy);
       boolean  atblockedcell=(2 == s.x && s.y == 2);
       return inmaze && !atblockedcell;
    }


}
