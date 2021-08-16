package java_ai_gym.models_common;


import java.util.List;

public interface Exploratory {


    State getState();
    int chooseBestAction(State state, EnvironmentParametersAbstract envParams);
    double findMaxQ(State state,EnvironmentParametersAbstract envParams);
    int chooseRandomAction(List<Integer> actions);
    int chooseAction(double fractionEpisodesFinished,EnvironmentParametersAbstract envParams);
    void writeMemory(State oldState, Integer Action, Double value,EnvironmentParametersAbstract envParams);
    double readMemory(State state, int Action,EnvironmentParametersAbstract envParams);



    //TODO move following methods to NNAgent
    double getRB_EPS();

    double getRB_ALP();

    double getBETA0();
}
