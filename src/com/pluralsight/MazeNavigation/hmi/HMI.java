package com.pluralsight.MazeNavigation.hmi;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.TabularMemory;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Maze;

import javax.swing.*;

public class HMI {

    public static void showMem(Agent agent, Maze maze, Showtype st) {
        Pos2d s = agent.status.getS();   //refers to state in agent status
        String txt;  String emptycelltxt;

        if (st.equals(Showtype.MAXQ))
             emptycelltxt="     ";
        else
             emptycelltxt=" ";

        for (int y = maze.maxy; y >= 1; y--) {
            for (int x = 1; x <= maze.maxx; x++) {
                s.setXY(x, y);
                Action aopt = agent.getAopt(s);  //setting state ach
                double Qs = agent.memory.readMem(s, aopt);
                if (maze.isStateBlocked(s) || maze.isStateTerminal(s))
                    txt = emptycelltxt;
                else {
                    if (st.equals(Showtype.MAXQ))
                        txt = String.format("%.3f", Qs);
                    else
                        txt = aopt.name();
                }

                System.out.print(txt + "   ");
            }
            System.out.println();
        }
    }
}
