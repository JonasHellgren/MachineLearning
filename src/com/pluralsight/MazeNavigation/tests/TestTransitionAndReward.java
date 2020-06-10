package com.pluralsight.MazeNavigation.tests;
import com.pluralsight.MazeNavigation.agent.State;
import com.pluralsight.MazeNavigation.enums.Action;
import com.pluralsight.MazeNavigation.environment.Environment;
import com.pluralsight.MazeNavigation.environment.Maze;
import org.junit.*;


public class TestTransitionAndReward {

    Maze maze=new Maze();
    State s=new State(1,1);
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
        s.setXY(1,1); State s2=new State(1,2);   env.Transition(s, Action.N);
        Assert.assertTrue(s.eq(s2));
    }
    @Test
    public void IsActionWfromX1Y1eqX1Y1() {
        s.setXY(1,1); State s2=new State(1,1);  env.Transition(s, Action.W);
        Assert.assertTrue(s.eq(s2));
    }
    @Test
    public void IsActionEfromX3Y3eqX4Y3() {
        s.setXY(3,3); State s2=new State(4,3);     env.Transition(s, Action.E);
        //System.out.println("s:"+s);
        Assert.assertTrue(s.eq(s2));
    }

}
