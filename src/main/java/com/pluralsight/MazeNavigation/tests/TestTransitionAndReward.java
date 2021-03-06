package com.pluralsight.MazeNavigation.tests;
import com.pluralsight.MazeNavigation.agent.Agentsetup;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Status;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.environment.Maze;
import org.junit.Assert;
import org.junit.Test;

public class TestTransitionAndReward {

    Maze maze=new Maze();
    Status agentstatus= new Status();
    Agentsetup agentsetup=new Agentsetup();
    Pos2d s=agentstatus.getS();   //refers to state in agent status
    Pos2d s2=new Pos2d(1,1);
    Environment env=new Environment();

    @Test
    public void IsX1Y1FeasPos() {
       s.setXY(1,1);    Assert.assertTrue("(1,1) ok",maze.isStateFeasible(s));
    }
    @Test
    public void IsX0Y1NotFeasPos() {
        s.setXY(0,1);    Assert.assertFalse("(0,1) no ok",maze.isStateFeasible(s));
    }
    @Test
    public void IsActionNfromX1Y1eqX1Y2() {
        s.setXY(1,1);  s2.setXY(1,2);
        agentstatus.setAch(Action.N);   env.Transition(agentstatus);
        Assert.assertTrue(s.eq(s2));
    }
    @Test
    public void IsActionWfromX1Y1eqX1Y1() {
        s.setXY(1,1); s2.setXY(1,1); agentstatus.setAch(Action.W); env.Transition(agentstatus);
        Assert.assertTrue(s.eq(s2));
    }
    @Test
    public void IsActionEfromX3Y3eqX4Y3() {
        s.setXY(3,3); s2.setXY(4,3);   agentstatus.setAch(Action.E); env.Transition(agentstatus);
        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsRot90ofX1Y0eqX0Y1() {
        s.setXY(1,0); s2.setXY(0,1);     s.rot(Math.PI/2);
        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsRot0ofX1Y0eqX0Y1() {
        s.setXY(1,0); s2.setXY(1,0);     s.rot(0.0);
        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsRotmin90ofX1Y0eqX0Y1() {
        s.setXY(1,0); s2.setXY(0,-1);     s.rot(-Math.PI/2);
        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsActionEfromX3Y3SometimeseqX3Y2() {
        s2.setXY(3,2);
        int nofEq=0;  int imax=10000;
        for (int i = 0; i <imax ; i++) {
            s.setXY(3,3);   agentstatus.setAch(Action.E); env.Transition(agentstatus);
            if (s.eq(s2))  nofEq++;
        }
        System.out.println("nofEq:"+nofEq);
        Assert.assertEquals(agentsetup.getPra()*imax,nofEq,agentsetup.getPra()*imax/2);
    }
    @Test
    public void IsRewardofActionEfromX3Y3eq1() {
        s.setXY(3,3);   agentstatus.setAch(Action.E); env.Transition(agentstatus);
        System.out.println("R:"+agentstatus.getR());
        Assert.assertEquals(1,agentstatus.getR(),0.1);
    }

    @Test
    public void IsRewardofActionNfromX3Y3eqminus() {
        s.setXY(3,3);   agentstatus.setAch(Action.N); env.Transition(agentstatus);
        System.out.println("R:"+agentstatus.getR());
        Assert.assertEquals(-0.04,agentstatus.getR(),0.01);
    }

    @Test
    public void IsRewardofActionEfromX3Y2eqminus1() {
        s.setXY(3,2);    agentstatus.setAch(Action.E); env.Transition(agentstatus);
        Assert.assertEquals(-1,agentstatus.getR(),0.1);
    }

    @Test
    public void IsNewStateActionEfromX3Y3eqX4Y3() {
        s.setXY(3,3);  s2.setXY(4,3);  agentstatus.setAch(Action.E); env.Transition(agentstatus);
        System.out.println("S:"+agentstatus.getS());
        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsNewStateActionNfromX3Y3eqX3Y3() {
        s.setXY(3,3);  s2.setXY(3,3);  agentstatus.setAch(Action.N); env.Transition(agentstatus);
        System.out.println("S:"+agentstatus.getS());
        Assert.assertTrue(s.eq(s2));
    }





}
