package com.pluralsight.MazeNavigation;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.hmi.HMI;

public class Main {

    public static void main(String[] args) {

        //create environment, settings with builder
        Environment env = new Environment.Builder()
                .defPwd(0.2).build();  //default setup, risk of non-desired direction
                //.defPwd(0.0).build();  //desired direction always realized
                //.defmaxx((byte) 6).defmaxy((byte) 5).build();  //larger world

        //create agent
        Agent agent = new Agent();
        Pos2d s = agent.status.getS();   //refer to state in agent status

        int nepismax=10000;  //number of episodes
        for (int nepis = 0; nepis < nepismax; nepis++) {
            s.setXY(1, 1);   //set start state, always lower-left cell
            while (!env.maze.isStateTerminal(s)) {
                agent.setup.setPra(agent.calcPra(nepis, nepismax));  //set probability for random action
                agent.chooseAction();   //action selection from present policy
                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
                agent.learnQ(env.maze);        //updating memory from experience
            }
        }

        //show results, i.e. agent policy represented in its memory
        HMI.showMem(agent, env.maze, Showtype.MAXQ);
        System.out.println();
        HMI.showMem(agent, env.maze, Showtype.BESTA);
    }

}
