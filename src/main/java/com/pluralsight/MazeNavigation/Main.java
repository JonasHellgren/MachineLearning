package com.pluralsight.MazeNavigation;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.enums.MemType;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.hmi.HMI;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;


public class Main {

    public static void main(String[] args) {

        //create environment, settings with builder
        Environment env = new Environment.Builder()
                .defPwd(0.2).build();  //default setup, risk of non-desired direction
        //.defPwd(0.0).build();  //desired direction always realized
        //.defmaxx((byte) 6).defmaxy((byte) 5).build();  //larger world

        //create agent
        Agent agent = new Agent();
        agent.nnmemory.net.setListeners(new ScoreIterationListener(1000));
        Pos2d s = agent.status.getS();   //refer to state in agent status

        boolean policyfromnn=true;
        int nstepsmax = 10000;    int nsteps = 0; //number of steps
        do {
            //System.out.println("nepis:"+nepis);
            int x = 1;  int y = 1;  s.setXY(x, y);   //set start state, always lower-left cell
            while (!env.maze.isStateTerminal(s) && nsteps < nstepsmax) {
                agent.setup.setPra(agent.calcPra(nsteps, nstepsmax));  //set probability for random action

                if (policyfromnn)
                    agent.chooseAction(agent.nnmemory);   //action selection from nn memory
                else
                    agent.chooseAction(agent.tabmemory);   //action selection from tab memory

                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
                agent.learnQ(env.maze, agent.tabmemory);        //updating memory from experience
                agent.learnNN(env.maze);        //updating memory from experience
                if (agent.repBuff.size() >= agent.nnmemory.RBLEN) {  nsteps++;      }
            }
        } while (nsteps<nstepsmax);

        //show results, i.e. agent policy represented in its memory
        System.out.println("Tabular memory");
        HMI.showMem(agent, env.maze, Showtype.MAXQ, MemType.Tab);
        System.out.println();
        HMI.showMem(agent, env.maze, Showtype.BESTA, MemType.Tab);

        System.out.println("NN memory");
        HMI.showMem(agent, env.maze, Showtype.MAXQ, MemType.NN);
        System.out.println();
        HMI.showMem(agent, env.maze, Showtype.BESTA, MemType.NN);

        agent.tabmemory.clearMem();

    }


}
