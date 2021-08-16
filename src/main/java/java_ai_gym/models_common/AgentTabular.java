package java_ai_gym.models_common;


import com.google.common.primitives.Doubles;

import java_ai_gym.models_sixrooms.SixRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
public abstract class AgentTabular implements Exploratory {

    private static final Logger logger = LoggerFactory.getLogger(AgentTabular.class);
    public State state;
    public double[][] Qsa;   //tabular memory

    private final Random random = new Random();
    public  double Q_INIT;
    public double GAMMA;  // gamma discount factor
    public  double ALPHA;  // learning rate
    public  double PROBABILITY_RANDOM_ACTION ;  //probability choosing random action
    public  int NUM_OF_EPISODES; // number of iterations

    @Override
    public State getState() {
        return state;
    }

    @Override
    public int chooseBestAction(State state,EnvironmentParametersAbstract envParams) {

        double[] QsVec = readMemory(state,envParams);
        double maxQ = -Double.MAX_VALUE;
        int bestAction = envParams.discreteActionsSpace.get(0);
        for (int action : envParams.discreteActionsSpace) {
            if (QsVec[envParams.getIdxAction(action)] > maxQ) {
                maxQ = QsVec[envParams.getIdxAction(action)];
                bestAction = envParams.discreteActionsSpace.get(envParams.getIdxAction(action));
            }
        }
        return bestAction;
    }

    @Override
    public double findMaxQ(State state,EnvironmentParametersAbstract envParams) {
        return Doubles.max(readMemory(state,envParams));
    }

    @Override
    public int chooseRandomAction(List<Integer> actions) {
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public int chooseAction(double fractionEpisodesFinished,EnvironmentParametersAbstract envParams) {
        return (Math.random() < PROBABILITY_RANDOM_ACTION) ?
                chooseRandomAction(envParams.getDiscreteActionsSpace()) :
                chooseBestAction(state,envParams);
    }

    @Override
    public void writeMemory(State state, Integer action, Double value,EnvironmentParametersAbstract envParams) {
        Qsa[envParams.getIdxState(state)][envParams.getIdxAction(action)] = value;
    }

    @Override
    public double readMemory(State state, int action,EnvironmentParametersAbstract envParams) {
        return Qsa[envParams.getIdxState(state)][envParams.getIdxAction(action)];
    }

    public double[] readMemory(State state,EnvironmentParametersAbstract envParams) {
        return Qsa[envParams.getIdxState(state)];
    }




}