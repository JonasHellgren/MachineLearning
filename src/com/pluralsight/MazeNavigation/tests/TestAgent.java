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
    TabularMemory memory= agent.memory;
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
        agent.chooseAction();
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
            agent.chooseAction();
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
        agent.chooseAction();  //setting state ach
        env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
        agent.learnQ();
        //System.out.println("Qsold:"+agent.memory.readMem(sold,Action.E));
        Assert.assertTrue(agent.memory.readMem(sold, Action.E)<1.0);
    }

    @Test
    public void learnMultiTrialsAtX3Y2() {
        s.setXY(3,2);
        memory.saveMem(s,Action.E,0.000);  //clear mem
        memory.saveMem(s,Action.N,0.0);    memory.saveMem(s,Action.S,0.0);
        memory.saveMem(s,Action.W,0.0);

        for (int i = 0; i < 10000; i++) {
        s.setXY(3,2);
        agent.chooseAction();  //setting state ach
        env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
        agent.learnQ();
        }
        System.out.println("QsoldE:"+agent.memory.readMem(sold,Action.E));
        System.out.println("QsoldN:"+agent.memory.readMem(sold,Action.N));
        Assert.assertTrue((agent.memory.readMem(sold, Action.E)<-0.5) &&
                (agent.memory.readMem(sold, Action.N)<-0.0) &&
                (agent.memory.readMem(sold, Action.E)<agent.memory.readMem(sold, Action.N))
        );
    }

    @Test
    public void learnMultiTrialsAtX3Y3() {
        s.setXY(3,3);
        memory.saveMem(s,Action.E,0.000);  //clear mem
        memory.saveMem(s,Action.N,0.0);    memory.saveMem(s,Action.S,0.0);
        memory.saveMem(s,Action.W,0.0);

        for (int i = 0; i < 10000; i++) {
            s.setXY(3,3);
            agent.chooseAction();  //setting state ach
            env.Transition(agent.status);   //updating s and setting sold=s, defining reward R
            agent.learnQ();
        }
        System.out.println("QsoldE:"+agent.memory.readMem(sold,Action.E));
        System.out.println("QsoldN:"+agent.memory.readMem(sold,Action.N));
        Assert.assertTrue((agent.memory.readMem(sold, Action.E)>0.5) &&
                (agent.memory.readMem(sold, Action.N)>0.0) &&
                (agent.memory.readMem(sold, Action.E)>agent.memory.readMem(sold, Action.N))
        );
    }



}
