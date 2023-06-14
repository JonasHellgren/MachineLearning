package multi_step_temp_diff.helpers;

import common.Counter;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

import java.util.HashMap;
import java.util.Map;

@Builder
public class NStepTDHelper<S> {

    private static final double ALPHA = 0.1;
    private static final int N = 2;
    private static final int MAX_VALUE = Integer.MAX_VALUE;
  //  private static final HashMap<Integer, StepReturn<S>> TIME_RETURN_MAP = new HashMap<>();
  //  private static final HashMap<Integer, Integer> TIME_STATE_MAP = new HashMap<>();
    private static final int TAU = 0;

    @Builder.Default
    public double alpha= ALPHA;  //learning rate
    @Builder.Default
    public int n= N;   //nof steps between state being updated and state bootstrapped
    @Builder.Default
    public int T= MAX_VALUE;  //time for termination
    @Builder.Default
    public int tau= TAU;  //the state visited at this time gets updated
    @Builder.Default
    public Map<Integer,StepReturn<S>> timeReturnMap= new HashMap<>();; //in episode experienced returns
    @Builder.Default
    public Map<Integer, StateInterface<S>> statesMap= new HashMap<>(); //in episode visited states
    @NonNull  public Counter episodeCounter;
    @NonNull  public Counter timeCounter;  //time step

    @Override
    public String toString () {
        return "T = " + T + " tau =" + tau +
                ", statesMap = " + statesMap +
                ", timeReturnMap = " + timeReturnMap.keySet() +
                " episodeCounter = " + episodeCounter.getCount() +
                " timeCounter = " + timeCounter.getCount();
    }

    public void reset() {
        statesMap.clear();
        timeReturnMap.clear();
        timeCounter.reset();
        T=MAX_VALUE;
        tau= TAU;
    }

}
