package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_mountaincar;

import swing.*;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Environment;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.EnvironmentParametersAbstract;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.StepReturn;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 *         Num    Observation               Min            Max
 *         0      Car Position              -1.2           0.6
 *         1      Car Velocity              -0.07          0.07
 *     Actions:
 *         Type: Discrete(3)
 *         Num    Action
 *         0      Accelerate to the Left
 *         1      Don't accelerate
 *         2      Accelerate to the Right
 *         Note: This does not affect the amount of velocity affected by the
 *         gravitational pull acting on the car.
 *     Reward:
 *          Reward of 0 is awarded if the agent reached the flag (position = 0.5)
 *          on top of the mountain.
 *          Reward of -1 is awarded if the position of the agent is less than 0.5.
 *     Starting State:
 *          The position of the car is assigned a uniform random value in
 *          [-0.6 , -0.4].
 *          The starting velocity of the car is always assigned to 0.
 *     Episode Termination:
 *          The car position is more than 0.5
 *          Episode length is greater than 200
 */

public class MountainCar implements Environment  {

    public MountainCar.EnvironmentParameters parameters = this.new EnvironmentParameters();

    private FrameEnvironment frame;
    public PanelMountainCar panel;
    final  int W=600;
    final  int H=300;  //frame size
    final int MARGIN=50;  //frame margin
    final  double CAR_RADIUS=0.05;


    // inner classes
    public class EnvironmentParameters extends EnvironmentParametersAbstract {

        public final double R_MOVE = -1.0;  //move transition
        public final double R_EXIT = 0;  //finding exit state reward

        public final double min_position = -1.2;
        public final double max_position = 0.6;
        public final double max_speed = 0.07;
        public final double goal_position = 0.5;
        public final double goal_velocity = 0;
        public final int max_nof_steps=200;

        public final double force = 0.001;
        public final double gravity = 0.0025;

        public int nofActions;
        public int minAction;



        public EnvironmentParameters() {
        }
    }

        //constructor
        public MountainCar() {
            parameters.continuousStateVariableNames.add("position");
            parameters.continuousStateVariableNames.add("velocity");
            parameters.discreteStateVariableNames.add("nofSteps");
            parameters.discreteActionsSpace.addAll(Arrays.asList(0, 1, 2));
            parameters.minAction = parameters.discreteActionsSpace.stream().min(Integer::compare).orElse(0);
            parameters.nofActions = parameters.discreteActionsSpace.size();

            frame=new FrameEnvironment();
            ScaleLinear xScaler=new ScaleLinear(parameters.min_position,parameters.max_position,MARGIN,W-MARGIN,false,MARGIN);
            ScaleLinear yScaler=new ScaleLinear(0,1,MARGIN,H-MARGIN,true,MARGIN);
            LineData roadData=createRoadData();
            Position2D carPositionInit=new Position2D(0.0,height(0.0));
            panel=new PanelMountainCar(xScaler,  yScaler,roadData, carPositionInit,CAR_RADIUS);
            frame.add(panel);
            frame.setSize(W, H);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel.repaint();
        }

    private LineData createRoadData() {

        final int NOF_POINTS=100;
        List<Double> xList=new ArrayList<>();
        List<Double> yList=new ArrayList<>();
        for (int i = 0; i < NOF_POINTS ; i++) {
            double f=(double) i/NOF_POINTS;
            double x=parameters.min_position*(1-f)+parameters.max_position*f;
            State state = new State();
            state.createContinuousVariable("position",x);
            double y=height(x);
            xList.add(x);
            yList.add(y);
        }
        return new LineData(
                xList.stream().mapToDouble(d -> d).toArray(),
                yList.stream().mapToDouble(d -> d).toArray());


    }


    @Override
    public EnvironmentParametersAbstract getParameters() {
        return parameters;
    }

    @Override
        public StepReturn step(int action, State state) {
            State newState = new State(state);
            StepReturn stepReturn = new StepReturn();
            double position=state.getContinuousVariable("position");
            double velocity=state.getContinuousVariable("velocity");
            velocity += (action - 1) * parameters.force + Math.cos(3 * position) * (-parameters.gravity);
            position += velocity;
            position = this.clip(position, parameters.min_position, parameters.max_position);
            velocity=(position <= parameters.min_position & velocity < 0)?0:velocity;
            newState.setVariable("position", position);
            newState.setVariable("velocity", velocity);
            stepReturn.state = newState;
            stepReturn.termState = isTerminalState(newState);
            stepReturn.reward = (stepReturn.termState)?0:-1.0;
            return stepReturn;
        }

        @Override
        public boolean isTerminalState(State state) {
            return (state.getContinuousVariable("position")>=parameters.goal_position &
                    state.getContinuousVariable("velocity")>=parameters.goal_velocity |
                    state.getDiscreteVariable("nofSteps")>=parameters.max_nof_steps);
        }

        public double height(State state) {
        return Math.sin(3 * state.getContinuousVariable("position")) * 0.45 + 0.55;
        }

        public double height(double position) {
            State state = new State();
            state.createContinuousVariable("position",position);
            return height(state);
        }



}
