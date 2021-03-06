package com.pluralsight.MazeNavigation.tests;

import com.pluralsight.MazeNavigation.agent.Agent;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.TabularMemory;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Environment;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestAgent {

    Agent agent=new Agent();
    TabularMemory memory= agent.tabmemory;
    Pos2d s=agent.status.getS();   //refers to state in agent status
    Pos2d sold=agent.status.getSold();   //refers to state in agent status
    Pos2d s2=new Pos2d(1,1);
    Environment env=new Environment();

    @Test
    public void chooseActionAtX1Y1() {
        s.setXY(1,1);
        memory.saveMem(s,Action.N,1.0);
        memory.saveMem(s,Action.E,0.5);
        memory.saveMem(s,Action.S,-0.1);
        memory.saveMem(s,Action.W,-0.2);
        agent.chooseAction(agent.tabmemory);
        //System.out.println("a:"+agent.status.getAch());
        Assert.assertEquals(Action.N,agent.status.getAch());
    }

    @Test
    public void multipleChooseActionAtX3Y3() {
        s.setXY(3,3);
        memory.saveMem(s,Action.N,0.0);
        memory.saveMem(s,Action.E,1.0);
        memory.saveMem(s,Action.S,-0.1);
        memory.saveMem(s,Action.W,-0.2);
        int nofactionS=0;
        for (int i = 0; i < 1000000; i++) {
            agent.chooseAction(agent.tabmemory);
            if (agent.status.getAch()==Action.S)
                nofactionS++;
        }
        //System.out.println("nofactionE:"+nofactionS);
        Assert.assertTrue(nofactionS>0);
    }

    @Test
    public void learnActionEAtX3Y2() {
        s.setXY(3,2);  memory.saveMem(s,Action.E,1.000);  //trick agent going E
        memory.saveMem(s,Action.N,0.0);    memory.saveMem(s,Action.S,0.0);
        memory.saveMem(s,Action.W,0.0);
        agent.chooseAction(agent.tabmemory);  //setting state ach
        env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
        agent.learnQ(env.maze,agent.tabmemory);
        //System.out.println("Qsold:"+agent.memory.readMem(sold,Action.E));
        Assert.assertTrue(agent.tabmemory.readMem(sold, Action.E)<1.0);
    }

    @Test
    public void learnMultiTrialsAtX3Y2() {
        s.setXY(3,2);
        memory.saveMem(s,Action.E,0.000);  //clear mem
        memory.saveMem(s,Action.N,0.0);    memory.saveMem(s,Action.S,0.0);
        memory.saveMem(s,Action.W,0.0);

        for (int i = 0; i < 10000; i++) {
        s.setXY(3,2);
        agent.chooseAction(agent.tabmemory);  //setting state ach
        env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
        agent.learnQ(env.maze,agent.tabmemory);
        }
        System.out.println("QsoldE:"+agent.tabmemory.readMem(sold,Action.E));
        System.out.println("QsoldN:"+agent.tabmemory.readMem(sold,Action.N));
        Assert.assertTrue((agent.tabmemory.readMem(sold, Action.E)<-0.5) &&
                (agent.tabmemory.readMem(sold, Action.N)<-0.0) &&
                (agent.tabmemory.readMem(sold, Action.E)<agent.tabmemory.readMem(sold, Action.N))
        );
    }


    @Test
    public void learnAtX3Y3() {
        Environment env = new Environment.Builder()
                .defPwd(0.0).build();
        int nepismax=30000; int nepis=0;
        do {
            s.setXY(3,3);
            while (!env.maze.isStateTerminal(s))   {
                agent.setup.setPra(agent.setup.getPrastart()+(agent.setup.getPraend()-agent.setup.getPrastart())*nepis/nepismax);
                agent.chooseAction(agent.tabmemory);   //action selection from present policy
                env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
                agent.learnQ(env.maze,agent.tabmemory);        //updating memory from experience
            }
            nepis++;
        } while (nepis<nepismax);

        System.out.println("QsoldN:"+agent.tabmemory.readMem(sold,Action.N));
        System.out.println("QsoldE:"+agent.tabmemory.readMem(sold,Action.E));
        System.out.println("QsoldS:"+agent.tabmemory.readMem(sold,Action.S));
        System.out.println("QsoldW:"+agent.tabmemory.readMem(sold,Action.W));

        Assert.assertEquals(1,agent.tabmemory.readMem(sold, Action.E),0.05);

        agent.clearMem();
        }

}
