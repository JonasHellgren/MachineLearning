package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import com.google.common.primitives.Doubles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Agent;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;

import java.util.List;
import java.util.Random;

/***
 * Agent implementation that solves simple toy problem: find exit room
 */

public class SixRoomsAgentTabular implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(SixRoomsAgentTabular.class);
    public State state;
    public double[][] Qsa;   //tabular memory
    private final SixRooms.EnvironmentParameters envParams;  //reference to environment parameters
    private final Random random = new Random();
    public final double Q_INIT = 0;

    public SixRoomsAgentTabular(SixRooms.EnvironmentParameters envParams) {
        this.envParams = envParams;
        state = new State();
        for (String varName : envParams.discreteStateVariableNames)
            state.createVariable(varName, envParams.INIT_DEFAULT_ROOM_NUMBER);

        logger.info("Five Rooms Agent Agent created. " + "nofStates:" + envParams.nofStates + ", nofActions:" + envParams.nofActions);
        createInitMemory(envParams);
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



    public void PrintQsa() {
        System.out.println("Qsa");
        State s = new State(state);
        for (int roomNr : envParams.discreteStateSpace) {
            for (int action : envParams.discreteActionsSpace) {
                s.setVariable("roomNumber", roomNr);
                System.out.printf("%.1f    ", Qsa[envParams.getIdxState(s)][envParams.getIdxAction(action)]);
            }
            System.out.println();
        }
    }

    public void PrintQsaBestAction() {
        System.out.println("Qsa(s,best action)");
        State s = new State(state);
        for (int roomNr : envParams.discreteStateSpace) {
            s.setVariable("roomNumber", roomNr);
            System.out.printf("roomNr:"+roomNr+", Q: %.1f    ", findMaxQ(s));
        }
        System.out.println();
    }

    public void showPolicy(SixRooms env) {
        // we consider every single state as a starting state
        // until we find the terminal state: we walk according to best action

        System.out.println("Policy for every state");
        for(int starRoomNr=0; starRoomNr<envParams.nofStates; starRoomNr++) {
            SixRooms.StepReturn stepReturn;
            state.setVariable("roomNumber", starRoomNr);
            System.out.print("Policy: " + state.getDiscreteVariable("roomNumber"));
            while (!env.isTerminalState(state)) {
                int bestA = chooseBestAction(state);
                stepReturn=env.step(bestA,state);
                state.copyState(stepReturn.state);
                System.out.print(" -> " + state.getDiscreteVariable("roomNumber"));
            }
            System.out.println();
        }
    }


}
