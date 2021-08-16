package java_ai_gym.models_common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EnvironmentParametersAbstract {
    public List<String> discreteStateVariableNames=new ArrayList<>();
    public Map<String,Integer> lowerBoundDiscreteStateVariables=new HashMap<>();
    public Map<String,Integer>  upperBoundDiscreteStateVariables=new HashMap<>();

    public List<String> continuousStateVariableNames=new ArrayList<>();
    public List<Integer> discreteActionsSpace=new ArrayList<>();

    protected abstract int getIdxState(State state);
    protected abstract  int getIdxAction(int action);

    public List<Integer> getDiscreteActionsSpace() {
        return discreteActionsSpace;
    }
}
