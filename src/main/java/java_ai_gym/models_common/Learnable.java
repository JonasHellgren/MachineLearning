package java_ai_gym.models_common;
import java.util.List;

/** Interface used by Agent classes */

public interface Learnable {
    State getState();
    int chooseBestAction(State state, EnvironmentParametersAbstract envParams);
    double findMaxQ(State state,EnvironmentParametersAbstract envParams);
    int chooseRandomAction(List<Integer> actions);
    int chooseAction(double fractionEpisodesFinished,EnvironmentParametersAbstract envParams);
    void writeMemory(State oldState, Integer Action, Double value,EnvironmentParametersAbstract envParams);
    double readMemory(State state, int Action,EnvironmentParametersAbstract envParams);
}
