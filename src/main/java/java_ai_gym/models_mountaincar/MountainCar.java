package java_ai_gym.models_mountaincar;

import java_ai_gym.models_common.Environment;
import java_ai_gym.models_common.EnvironmentParametersAbstract;
import java_ai_gym.models_common.State;
import java_ai_gym.models_common.StepReturn;
import java_ai_gym.swing.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 *         Num    Observation               Min            Max
 *         0      Car Position              -1.2           0.6
 *         1      Car Velocity              -0.07          0.07
 *
 *         min { sin(3*x) * 0.45 + 0.55 },   x=-1.2..0.6 => x=-0.5235
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

public class MountainCar extends Environment {

    public MountainCar.EnvironmentParameters parameters = this.new EnvironmentParameters();
    final  int FRAME_WEIGHT =600;
    final  int FRAME_HEIGHT =300;
    final int FRAME_MARGIN =50;  //frame margin

    public PanelMountainCarAnimation animationPanel;
    public PanelMountainCarPlot plotPanel;
    public JLabel  label;
    final  double CAR_RADIUS=0.05;
    final  int CIRCLE_RADIUS_IN_DOTS =10;


    // inner classes
    public class EnvironmentParameters extends EnvironmentParametersAbstract {

        public final double MIN_POSITION = -1.2;
        public final double MAX_POSITION = 0.6;
        public  double MIN_START_POSITION = -0.6;  // -0.6;
        public  double MAX_START_POSITION = 0.5;  //-0.4;
        public final double POSITION_AT_MIN_HEIGHT = -0.5235;

        public double MIN_START_VELOCITY = -0.03;
        public double MAX_START_VELOCITY = 0.03;

        public final double MAX_SPEED = 0.07;
        public final double GOAL_POSITION = 0.5;
        public final double GOAL_VELOCITY = 0;
        public final int MAX_NOF_STEPS =200;
        public  double NON_TERMINAL_REWARD = -1.0;

        public final double FORCE = 0.001;
        public final double GRAVITY = 0.0025;

        public int NOF_ACTIONS;
        public int MIN_ACTION;

        public double startPosition;
        public double startVelocity=0;


        public EnvironmentParameters() {
        }

        @Override
        protected int getIdxState(State state) {
            return 0;
        }

        @Override
        protected int getIdxAction(int action) {
            return 0;
        }
    }

    //constructor
    public MountainCar() {
        parameters.continuousStateVariableNames.add("position");
        parameters.continuousStateVariableNames.add("velocity");
        parameters.discreteStateVariableNames.add("nofSteps");
        parameters.discreteActionsSpace.addAll(Arrays.asList(0, 1, 2));
        parameters.MIN_ACTION = parameters.discreteActionsSpace.stream().min(Integer::compare).orElse(0);
        parameters.NOF_ACTIONS = parameters.discreteActionsSpace.size();

        LineData roadData=createRoadData();
        setStartPositionAndVelocity();
        setupFrameAndPanel(roadData);
        animationPanel.repaint();
    }

    private void setupFrameAndPanel(LineData roadData) {
        animationFrame =new FrameEnvironment(FRAME_WEIGHT, FRAME_HEIGHT,"MountainCar animation");
        plotFrame =new FrameEnvironment(FRAME_WEIGHT, FRAME_HEIGHT,"MountainCar plots");
        ScaleLinear xScaler=new ScaleLinear(parameters.MIN_POSITION,parameters.MAX_POSITION,
                FRAME_MARGIN, FRAME_WEIGHT - FRAME_MARGIN,
                false, FRAME_MARGIN);
        ScaleLinear yScaler=new ScaleLinear(0,1, FRAME_MARGIN,
                FRAME_HEIGHT - FRAME_MARGIN,true, FRAME_MARGIN);

        Position2D carPositionInit=new Position2D(parameters.startPosition,height(parameters.startPosition));
        animationPanel =new PanelMountainCarAnimation(xScaler,yScaler, roadData, carPositionInit,CAR_RADIUS);
        label = new JLabel();
        animationPanel.label=label;
        label.setText("label text");
        animationPanel.add(label);
        animationFrame.add(animationPanel);
        animationFrame.setVisible(true);

        animationFrame.add(animationPanel);
        animationFrame.setVisible(true);

        //todo replavce with PanelMountainCarPlot
        List<Position2D> circlePositionList=new ArrayList<>();
        List<Integer> actionList=new ArrayList<>();
        circlePositionList.add(new Position2D(0.5,0));
        actionList.add(1);

        ScaleLinear yScalerVelocity=new ScaleLinear(-parameters.MAX_SPEED,parameters.MAX_SPEED,
                FRAME_MARGIN,FRAME_HEIGHT - FRAME_MARGIN,true, FRAME_MARGIN);
        plotPanel =new PanelMountainCarPlot(xScaler,yScalerVelocity, circlePositionList, actionList, CIRCLE_RADIUS_IN_DOTS);
        plotFrame.add(plotPanel);
        plotFrame.setVisible(true);



    }

    private void setStartPositionAndVelocity() {
        parameters.startPosition=calcRandomFromIntervall(parameters.MIN_START_POSITION,parameters.MAX_START_POSITION);
        parameters.startVelocity= calcRandomFromIntervall(parameters.MIN_START_VELOCITY,parameters.MAX_START_VELOCITY);
    }

    private double calcRandomFromIntervall(double minValue, double maxValue) {
        return minValue+Math.random()*(maxValue-minValue);
    }

    /* private double calcRandomPosition() {
        return parameters.MIN_POSITION+
                Math.random()*(parameters.MAX_POSITION-parameters.MIN_POSITION);
    }

    private double calcRandomVelocity() {
        double minSpeed=-parameters.MAX_SPEED;
        return  minSpeed+
                Math.random()*(parameters.MAX_SPEED-minSpeed);
    }  */

    private LineData createRoadData() {

        final int NOF_POINTS=100;
        List<Double> xList=new ArrayList<>();
        List<Double> yList=new ArrayList<>();
        for (int i = 0; i < NOF_POINTS ; i++) {
            double f=(double) i/NOF_POINTS;
            double x=parameters.MIN_POSITION *(1-f)+parameters.MAX_POSITION *f;
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
    public StepReturn step(int action, State state) {
        State newState = new State(state);
        StepReturn stepReturn = new StepReturn();
        double position=state.getContinuousVariable("position");
        double velocity=state.getContinuousVariable("velocity");
        velocity += (action - 1) * parameters.FORCE + Math.cos(3 * position) * (-parameters.GRAVITY);
        position += velocity;
        position = this.clip(position, parameters.MIN_POSITION, parameters.MAX_POSITION);
        velocity=(position <= parameters.MIN_POSITION & velocity < 0)?0:velocity;
        newState.setVariable("position", position);
        newState.setVariable("velocity", velocity);
        newState.setVariable("nofSteps", state.getDiscreteVariable("nofSteps")+1);
        stepReturn.state = newState;
        stepReturn.termState = isTerminalState(newState);
        stepReturn.reward = (stepReturn.termState)?
        //stepReturn.reward = isGoalState(stepReturn)?
                0:
                parameters.NON_TERMINAL_REWARD;

        state.nofSteps++;
        return stepReturn;
    }

    public boolean isGoalState(StepReturn stepReturn) {
        return (stepReturn.termState &
                stepReturn.state.getDiscreteVariable("nofSteps")<parameters.MAX_NOF_STEPS);
    }

    public void initState(State state) {
        setStartPositionAndVelocity();
        state.createContinuousVariable("position",parameters.startPosition);
        state.createContinuousVariable("velocity",parameters.startVelocity);
        state.createDiscreteVariable("nofSteps",0);
    }

    public void setRandomStateValuesStart(State state) {
        setRandomStateValues(state, true);
    }

    public void setRandomStateValuesAny(State state) {
        setRandomStateValues(state, false);
    }


    private void setRandomStateValues(State state, boolean startState) {

        if (startState) {
            state.setVariable("position", calcRandomFromIntervall(parameters.MIN_START_POSITION, parameters.MAX_START_POSITION));
            state.setVariable("velocity", calcRandomFromIntervall(parameters.MIN_START_VELOCITY, parameters.MAX_START_VELOCITY));
        } else {
            state.setVariable("position", calcRandomFromIntervall(parameters.MIN_POSITION, parameters.MAX_POSITION));
            state.setVariable("velocity", calcRandomFromIntervall(-parameters.MAX_SPEED, parameters.MAX_SPEED));
        }

        state.setVariable("nofSteps", 0);
    }


    @Override
    public boolean isTerminalState(State state) {
        return (state.getContinuousVariable("position")>=parameters.GOAL_POSITION &
                state.getContinuousVariable("velocity")>=parameters.GOAL_VELOCITY |
                state.getDiscreteVariable("nofSteps")>=parameters.MAX_NOF_STEPS);
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