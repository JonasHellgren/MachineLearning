package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_fiverooms;

import com.google.common.primitives.Doubles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.Agent;
import udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common.State;

import java.util.List;
import java.util.Random;

public class FiveRoomsAgentTabular implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(FiveRoomsAgentTabular.class);
    public State state;
    public double Qsa[][];
    private final FiveRooms.EnvironmentParameters envParams;  //reference to environment parameters
    private final Random random=new Random();


    public FiveRoomsAgentTabular(FiveRooms.EnvironmentParameters envParams) {

        this.envParams=envParams;
        state=new State();
        for (String varName:envParams.discreteStateVariableNames)
            state.createVariable(varName,envParams.INIT_DEFAULT_ROOM_NUMBER);

        logger.info("Five Rooms Agent Agent created. "+"nofStates:"+envParams.nofStates+", nofActions:"+envParams.nofActions);
        createInitMemory(envParams);
    }

    @Override
    public int chooseBestAction(State state) {

        double[] QsVec=readMemory(state);
        double maxQ = envParams.R_FAIL;
        int bestAction=0;
        for (int action:envParams.discreteActionsSpace) {
            if (QsVec[envParams.getIdxAction(action)]>maxQ) {
                maxQ=QsVec[envParams.getIdxAction(action)];
                bestAction=envParams.discreteActionsSpace.get(envParams.getIdxAction(action));
            }
        }
        return bestAction;
    }

    @Override
    public double findMaxQ(State state) {
        return Doubles.max(readMemory(state));
    }

    @Override
    public int chooseRandomAction(List<Integer> aSet) {
        return aSet.get(random.nextInt(aSet.size()));
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

    private void createInitMemory(FiveRooms.EnvironmentParameters envParams) {
        Qsa=new double[envParams.nofStates][envParams.nofActions];
        State s=new State(state);
        for(int idxState = 0; idxState< envParams.nofStates; ++idxState) {
            for (int idxAction = 0; idxAction < envParams.nofActions; ++idxAction) {
                s.setVariable("roomNumber", envParams.stateSpace.get(idxState));
                writeMemory(s, envParams.discreteActionsSpace.get(idxAction),0d);
            }
        }
    }

    public void PrintQsa() {
        for (int state = 0; state < envParams.stateSpace.size(); ++state) {
            for (int action = 0; action < envParams.discreteActionsSpace.size(); ++action) {
                    System.out.printf("%.1f    ", Qsa[state][action]);
            }
            System.out.println();
        }
    }

}
