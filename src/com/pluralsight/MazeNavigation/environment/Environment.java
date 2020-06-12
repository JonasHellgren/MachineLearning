package com.pluralsight.MazeNavigation.environment;

import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Status;
import com.pluralsight.MazeNavigation.enums.Action;

public class Environment {
    public Maze maze;   //declaration

    public Environment() {  //constructor
        maze = new Maze();  //initiation
    }

    public void Transition(Status status) {
        Pos2d s = status.getS();  //s refers to agent position state
        Pos2d sold = status.getSold();  //s refers to agent old position state
        Pos2d ds = new Pos2d(1, 1);   //the "original" intention of position change, resulting from the action a
        Pos2d dsrot = new Pos2d(1, 1);  //the position change resulting from the action a and rotation disturbance
        Action a = status.getAch();

        //Define sold
        sold.setS(s);   //sold is s

        //interpret a
        switch (a) {
            case N -> ds.setXY(0, 1);
            case E -> ds.setXY(1, 0);
            case S -> ds.setXY(0, -1);
            case W -> ds.setXY(-1, 0);
        }

        //define dsrot, change in position, equal to matrix rotation of ds
        dsrot.setS(ds);  //copy ds into dsrot
        double r = (Math.random() * 1);  //random number in [0,1]
        if ((0 <= r) && (r <= maze.Pwd / 2))
            dsrot.rot(Math.PI / 2);
        else if ((maze.Pwd / 2 <= r) && (r <= maze.Pwd))
            dsrot.rot(-Math.PI / 2);
        else
            dsrot.rot(0.0);


        //Define update of s
        Pos2d sint = new Pos2d(s);
        sint.setS(s);
        sint.add(dsrot);

        if (maze.isStateFeasible(sint))
            s.setS(sint);
        else
            s.setS(s);

        //Calculate and set reward
        status.setR(CalcReward(status));

    }

    private double CalcReward(Status status) {
        Pos2d s = status.getS();  //s refers to agent position state
        Pos2d sold = status.getSold();  //s refers to agent old position state
        double goal = maze.isStateGoal(s) ? 1.0 : 0.0;
        double trap = maze.isStateTrap(s) ? 1.0 : 0.0;
        double movpen;
        //if (s.eq(sold))  movpen=0; else
        if (maze.isStateTerminal(s))  movpen=0; else
        movpen=-0.04;
        return movpen + 1 * goal - 1 * trap;
    }


}
