package com.pluralsight.MazeNavigation.tests;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Status;
import com.pluralsight.MazeNavigation.agent.TabularMemory;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Environment;
import org.junit.Assert;
import org.junit.Test;
import com.pluralsight.MazeNavigation.generalfunctions.*;

import java.util.Random;

public class TestTabularMemory {

    Agent agent=new Agent();
    TabularMemory memory= agent.tabmemory;
    Pos2d s=agent.status.getS();   //refers to state in agent status
    Pos2d s2=new Pos2d(1,1);
    Environment env=new Environment();

    @Test
    public void AreKeysForX1Y1AWandX2Y1AWDifferent() {
        s.setXY(1,1);  s2.setXY(2,1);
        Assert.assertNotEquals(memory.getKey(s, Action.W),memory.getKey(s2,Action.W));
    }

    @Test
    public void AreKeysForManyRandomSAifferent() {
        int nofeqkeys=0;

        for (int i = 0; i < 1000 ; i++) {
        int x1=randomNumberInRange.get(1,env.maze.maxx);  //random number in [1,2,..,maxx]
        int y1=randomNumberInRange.get(1,env.maze.maxy);  //random number in [1,2,..,maxx]
        int x2=randomNumberInRange.get(1,env.maze.maxx);  //random number in [1,2,..,maxx]
        int y2=randomNumberInRange.get(1,env.maze.maxy);  //random number in [1,2,..,maxx]
        Action a1=Action.values()[new Random().nextInt(Action.values().length)];
        Action a2;
        do {  //repeat until different action found
         a2=Action.values()[new Random().nextInt(Action.values().length)]; }
         while (a1==a2);
        s.setXY(x1,x1);  s2.setXY(x2,x2);
        int key1= memory.getKey(s, a1);   int key2 = memory.getKey(s2, a2);

        if (key1==key2)  nofeqkeys++;  //ooops, equal keys
        }

        Assert.assertEquals(0,nofeqkeys);
    }

    @Test
    public void BlancMemoryReadAtX1Y1AW() {
        s.setXY(1,1);  Action a=Action.values()[new Random().nextInt(Action.values().length)];
        Assert.assertEquals(0.0,memory.readMem(s, a),0.01);
    }

    @Test
    public void SaveAndReadAtX1Y1AW() {
        s.setXY(1,1);  Action a=Action.values()[new Random().nextInt(Action.values().length)];
        memory.saveMem(s, a,10.0);
        System.out.println("a:"+a);
        Assert.assertEquals(10.0,memory.readMem(s, a),0.01);
    }

}
