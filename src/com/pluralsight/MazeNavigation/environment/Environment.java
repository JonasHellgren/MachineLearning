package com.pluralsight.MazeNavigation.environment;

import com.pluralsight.MazeNavigation.agent.State;
import com.pluralsight.MazeNavigation.enums.Action;

public class Environment {
    public Maze maze;   //declaration

    public Environment () {  //constructor
        maze = new Maze();  //initiation
    }

    public void Transition(State s, Action a) {
        State dsint = new State(1,1);

        switch (a) {
            case N -> dsint.setXY(0,1);
            case E -> dsint.setXY(1,0);
            case S -> dsint.setXY(0,-1);
            case W -> dsint.setXY(-1,0);
        }

    //Matrix rotation


    //Define snew
    State sint = new State(s);
    sint.setS(s); sint.add(dsint);

        if (maze.isStateFeasible(sint))
            s.setS(sint);
        else
            s.setS(s);


    }


}
