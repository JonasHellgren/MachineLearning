package java_ai_gym.models_sixrooms;


import com.google.common.primitives.Doubles;

import java_ai_gym.models_common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
public class SixRoomsAgentTabular implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(SixRoomsAgentTabular.class);
    public State state;
    public double[][] Qsa;   //tabular memory
    private final SixRooms.EnvironmentParameters envParams;  //reference to environment parameters
    private final Random random = new Random();
    public final double Q_INIT = 0;
    public final double GAMMA = 1.0;  // gamma discount factor
    public final double ALPHA = 0.1;  // learning rate
    public final double PROBABILITY_RANDOM_ACTION = 0.2;  //probability choosing random action
    public final int NUM_OF_EPISODES = 10000; // number of iterations

    public SixRoomsAgentTabular(SixRooms.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        for (String varName : envParams.discreteStateVariableNames)
            state.createDiscreteVariable(varName, envParams.INIT_DEFAULT_ROOM_NUMBER);

        logger.info("Tabular based six rooms agent created. " + "nofStates:" + envParams.nofStates + ", nofActions:" + envParams.nofActions);
        createInitMemory(envParams);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public double getRB_EPS() {
        return 0;
    }

    @Override
    public double getRB_ALP() {
        return 0;
    }

    @Override
    public double getBETA0() {
        return 0;
    }

    @Override
    public int chooseBestAction(State state) {

        double[] QsVec = readMemory(state);
        double maxQ = envParams.R_FAIL;
        int bestAction = 0;
        for (int action : envParams.discreteActionsSpace) {
            if (QsVec[envParams.getIdxAction(action)] > maxQ) {
                maxQ = QsVec[envParams.getIdxAction(action)];
                bestAction = envParams.discreteActionsSpace.get(envParams.getIdxAction(action));
            }
        }
        return bestAction;
    }

    @Override
    public double findMaxQ(State state) {
        return Doubles.max(readMemory(state));
    }

    @Override
    public int chooseRandomAction(List<Integer> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public int chooseAction(double fractionEpisodesFinished) {
        return (Math.random() < PROBABILITY_RANDOM_ACTION) ?
                chooseRandomAction(envParams.discreteActionsSpace) :
                chooseBestAction(state);
    }

    @Override
    public void writeMemory(State state, Integer action, Double value) {
        Qsa[envParams.getIdxState(state)][envParams.getIdxAction(action)] = value;
    }

    @Override
    public double readMemory(State state, int action) {
        return Qsa[envParams.getIdxState(state)][envParams.getIdxAction(action)];
    }

    public double[] readMemory(State state) {
        return Qsa[envParams.getIdxState(state)];
    }

    private void createInitMemory(SixRooms.EnvironmentParameters envParams) {
        Qsa = new double[envParams.nofStates][envParams.nofActions];
        State s = new State(state);
        for (int state : envParams.discreteStateSpace) {
            for (int action : envParams.discreteActionsSpace) {
                s.setVariable("roomNumber", state);
                writeMemory(s, action, Q_INIT);
            }
        }
    }


}