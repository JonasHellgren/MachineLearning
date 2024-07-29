package qlearning_objoriented_class_sceleton.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class State {

    private static final Logger logger = Logger.getLogger(State.class.getName());

    Map<String,Integer> discreteVariables = new HashMap<>();
    Map<String,Double> continuousVariables = new HashMap<>();

    public State() {
    }

    public State(State state) {
        this.discreteVariables.putAll(state.discreteVariables);
        this.continuousVariables.putAll(state.continuousVariables);
    }

    public void createVariable(String name, Integer value) {
        discreteVariables.put(name,value);
    }

    public void setVariable(String name,Integer value) {

        if (!discreteVariables.containsKey(name))
            logger.warning("Error when setting stateNew variable, following variable does not exist:"+name);
        else
            discreteVariables.replace(name,value);
    }

    public Integer getDiscreteVariable(String name) {

        if (!discreteVariables.containsKey(name)) {
            logger.warning("Error when getting stateNew variable, following variable does not exist:" + name);
            return null;
        }
        else
            return discreteVariables.get(name);
    }

    public void createVariable(String name,Double value) {
        continuousVariables.put(name,value);
    }

    public void setVariable(String name,Double value) {

        if (!continuousVariables.containsKey(name))
            logger.warning("Error when setting stateNew variable, following variable does not exist:"+name);
        else
            continuousVariables.replace(name,value);
    }

    public Double getContinuousVariable(String name) {

        if (!continuousVariables.containsKey(name)) {
            logger.warning("Error when getting stateNew variable, following variable does not exist:" + name);
            return null;
        }
        else
            return continuousVariables.get(name);
    }



    @Override
    public String toString() {
        List<String> discreteVariableKeyValuePairs=new ArrayList<>();
        List<String> continuousVariableKeyValuePairs=new ArrayList<>();

        for (String name:discreteVariables.keySet())
            discreteVariableKeyValuePairs.add(name+"="+discreteVariables.get(name));

        for (String name:continuousVariables.keySet())
            continuousVariableKeyValuePairs.add(name+"="+continuousVariables.get(name));

        return "State{" +
                "discreteVariables:" +
                discreteVariableKeyValuePairs +
                ", continuousVariables:" +
                continuousVariableKeyValuePairs +
                '}';
    }
}
