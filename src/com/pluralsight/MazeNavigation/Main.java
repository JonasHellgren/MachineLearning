package com.pluralsight.MazeNavigation;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.environment.Maze;
import com.pluralsight.MazeNavigation.hmi.HMI;

public class Main {

    public static void main(String[] args) {

    //create environement
    Environment env=new Environment();

	//create agent, settings with builder
    Agent agent=new Agent();
    Pos2d s=agent.status.getS();   //refers to state in agent status

    int nepismax=100000;

    int nepis=0;
    do {
        s.setXY(2,1);
        while (!env.maze.isStateTerminal(s))   {
        agent.setup.setPra(agent.setup.getPrastart()+(agent.setup.getPraend()-agent.setup.getPrastart())*nepis/nepismax);
        agent.chooseAction();
        env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
        agent.learnQ();        //updating memory from experience
        }
        nepis++;
    } while (nepis<nepismax);

    HMI.showMem(agent,env.maze, Showtype.MAXQ);
    System.out.println();
    HMI.showMem(agent,env.maze, Showtype.BESTA);
  }

}
