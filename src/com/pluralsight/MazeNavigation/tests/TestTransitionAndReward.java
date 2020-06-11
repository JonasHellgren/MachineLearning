package com.pluralsight.MazeNavigation.tests;
import com.pluralsight.MazeNavigation.agent.Pos2d;
import com.pluralsight.MazeNavigation.agent.Status;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.environment.Maze;
import org.junit.*;


public class TestTransitionAndReward {

    Maze maze=new Maze();
    Status agentstatus= new Status();
    Pos2d s=agentstatus.getS();   //refers to state in agent status
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
        s.setXY(1,1); Pos2d s2=new Pos2d(1,2);   env.Transition(agentstatus, Action.N);
        Assert.assertTrue(s.eq(s2));
    }
    @Test
    public void IsActionWfromX1Y1eqX1Y1() {
        s.setXY(1,1); Pos2d s2=new Pos2d(1,1);  env.Transition(agentstatus, Action.W);
        Assert.assertTrue(s.eq(s2));
    }
    @Test
    public void IsActionEfromX3Y3eqX4Y3() {
        s.setXY(3,3); Pos2d s2=new Pos2d(4,3);     env.Transition(agentstatus, Action.E);

        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsRot90ofX1Y0eqX0Y1() {
        s.setXY(1,0); Pos2d s2=new Pos2d(0,1);     s.rot(Math.PI/2);
        Assert.assertTrue(s.eq(s2));
    }

    @Test
    public void IsActionEfromX3Y3SometimeseqX3Y2() {
        Pos2d s2=new Pos2d(3,2);
        int nofEq=0;
        for (int i = 0; i <10000 ; i++) {
            s.setXY(3,3);     env.Transition(agentstatus, Action.E);
            if (s.eq(s2))  nofEq++;
        }
        Assert.assertTrue("nofeq:"+nofEq,nofEq>10);
    }
    @Test
    public void IsRewardofActionEfromX3Y3eq0d9() {
        s.setXY(3,3);     env.Transition(agentstatus, Action.E);
        Assert.assertTrue(agentstatus.getR()==0.9);
    }

    @Test
    public void IsRewardofActionEfromX3Y3eqminus1d1() {
        s.setXY(3,2);     env.Transition(agentstatus, Action.E);
        Assert.assertTrue(agentstatus.getR()==-1.1);
    }





}
