package com.pluralsight.MazeNavigation.hmi;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.enums.MemType;
import com.pluralsight.MazeNavigation.enums.Showtype;
import com.pluralsight.MazeNavigation.environment.Maze;

public class HMI {

    public static void showMem(Agent agent, Maze maze, Showtype st, MemType mt) {
        //This method shows the agent policy
        //needs to use concrete memory class
        Pos2d s = agent.status.getS();   //refers to state in agent status
        String txt;  String emptycelltxt;
        double Qs;  Action aopt;

        if (st.equals(Showtype.MAXQ))
             emptycelltxt="     ";
        else
             emptycelltxt=" ";

        for (int y = maze.maxy; y >= 1; y--) {
            for (int x = 1; x <= maze.maxx; x++) {
                s.setXY(x, y);
                if (mt.equals(MemType.Tab))
                {
                aopt = agent.getAopt(s,agent.tabmemory);  //setting state ach
                Qs = agent.tabmemory.readMem(s, aopt); }
                else
                {
                    aopt = agent.getAopt(s,agent.nnmemory);  //setting state ach
                    Qs = agent.nnmemory.readMem(s, aopt); }


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
