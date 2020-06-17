package com.pluralsight.MazeNavigation.environment;

import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Status;
import com.pluralsight.MazeNavigation.enums.Action;

public class Environment {  //The environment can be seen as the major host of other objects.
    //It does for example give the agent reward, i.e. feedback, of taken actions.

    //The builder class  is used to modify settings of Pwd, maxx and maxy.
    public static class Builder {
        private double Pwd=0.2;   //default value of probability of taking non desired direction
        private byte maxx=4;  //default value of world size in x-direction
        private byte maxy=3;  //default value of world size in y-direction

     public Builder() {};  //constructor
    public Builder defPwd(double Pwd)  {this.Pwd=Pwd; return this;}
    public Builder defmaxx(byte maxx)  {this.maxx=maxx; return this;}
    public Builder defmaxy(byte maxy)  {this.maxy=maxy; return this;}

    public    Environment build() {
        Environment env=new Environment();
        env.maze.Pwd=this.Pwd;  env.maze.maxx=this.maxx;  env.maze.maxy=this.maxy;
        return env;
        }
    }

    public Maze maze;   //declaration

    public Environment() {  //constructor
        maze = new Maze();
    }

    public void Transition(Status status) {  //This method sets updates status in agent, i.e. defines state, reward etc.
        Pos2d s = status.getS();  //s refers to agent position state
        Pos2d sold = status.getSold();  //s refers to agent old position state
        Pos2d ds = new Pos2d(1, 1);   //the "original" intention of position change, resulting from the action a
        Pos2d dsrot = new Pos2d(1, 1);  //the position change resulting from the action a and rotation disturbance
        Action a = status.getAch();

        //Define sold, must be done before s is updated
        sold.setS(s);   //sold is s

        //interpret a
        switch (a) {
            case N -> ds.setXY(0, 1);  //move upwards in y-direction
            case E -> ds.setXY(1, 0);  //move right in x-direction
            case S -> ds.setXY(0, -1);  //move left in x-direction
            case W -> ds.setXY(-1, 0);  //move downwards in y-direction
        }

        //define dsrot, change in position, equal to matrix rotation of ds
        dsrot.setS(ds);  //copy ds into dsrot
        double r = (Math.random() * 1);  //random number in [0,1]
        if ((0 <= r) && (r <= maze.Pwd / 2))
            dsrot.rot(Math.PI / 2);  //90 degrees counter clockwise
        else if ((maze.Pwd / 2 <= r) && (r <= maze.Pwd))
            dsrot.rot(-Math.PI / 2);  //90 degrees  clockwise
        else
            dsrot.rot(0.0);  //no rotation

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

    private double CalcReward(Status status) { //This method calculates the reward for transition to state s
        Pos2d s = status.getS();  //s refers to agent position state
        if (maze.isStateGoal(s))
            return 1;
        else if  (maze.isStateTrap(s))
            return -1;
        else
            return -0.04;

    }
}
