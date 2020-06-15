package com.pluralsight.MazeNavigation;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.hmi.HMI;

public class Main {

    public static void main(String[] args) {

        //create environement
        Environment env = new Environment();

        //create agent, settings with builder
        Agent agent = new Agent();
        Pos2d s = agent.status.getS();   //refer to state in agent status

        int nepismax=10000;  //number of episodes
        for (int nepis = 0; nepis < nepismax; nepis++) {
            s.setXY(1, 1);   //set start state, always lower-left cell
            while (!env.maze.isStateTerminal(s)) {
                agent.setup.setPra(agent.calcPra(nepis, nepismax));
                agent.chooseAction();   //action selection from present policy
                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
                agent.learnQ(env.maze);        //updating memory from experience
            }
        }

        HMI.showMem(agent, env.maze, Showtype.MAXQ);
        System.out.println();
        HMI.showMem(agent, env.maze, Showtype.BESTA);
    }

}
