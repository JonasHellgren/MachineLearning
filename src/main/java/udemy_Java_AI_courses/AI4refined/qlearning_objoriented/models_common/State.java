package udemy_Java_AI_courses.AI4refined.qlearning_objoriented.models_common;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * Generic state class, can include discrete and/or continuous variables.
 * The variable naming is flexible thanks to hash maps.
 */

public class State {
    private static final Logger logger = LoggerFactory.getLogger(State.class);

    Map<String, Integer> discreteVariables = new HashMap<>();
    Map<String, Double> continuousVariables = new HashMap<>();

    public State() {
    }

    public State(State state) {
        copyState(state);
    }

    public void copyState(State state) {
        this.discreteVariables.putAll(state.discreteVariables);
        this.continuousVariables.putAll(state.continuousVariables);
    }

    public void createVariable(String name, Integer value) {
        discreteVariables.put(name, value);
    }

    public void setVariable(String name, Integer value) {

        if (!discreteVariables.containsKey(name))
            logger.error("Error when setting state variable, following variable does not exist:" + name);
        else
            discreteVariables.replace(name, value);
    }

    public Integer getDiscreteVariable(String name) {

        if (!discreteVariables.containsKey(name)) {
            logger.error("Error when getting state variable, following variable does not exist:" + name);
            return null;
        } else
            return discreteVariables.get(name);
    }

    public void createVariable(String name, Double value) {
        continuousVariables.put(name, value);
    }

    public void setVariable(String name, Double value) {

        if (!continuousVariables.containsKey(name))
            logger.error("Error setting state variable, it does not exist:" + name);
        else
            continuousVariables.replace(name, value);
    }

    public Double getContinuousVariable(String name) {

        if (!continuousVariables.containsKey(name)) {
            logger.error("Error getting state variable, it does not exist:" + name);
            return null;
        } else
            return continuousVariables.get(name);
    }

    public INDArray getStateVariablesAsNetworkInput(EnvironmentParametersAbstract envParams) {

        int nofFeatures=0;
        List<Double> varValues=new ArrayList<>();
        for (String varName:envParams.discreteStateVariableNames) {
            varValues.add((double) getDiscreteVariable(varName));
            nofFeatures++;
        }

        for (String varName:envParams.continuousStateVariableNames) {
            varValues.add(getContinuousVariable(varName));
            nofFeatures++;
        }

        double[] array = varValues.stream().mapToDouble(d -> d).toArray();
        return Nd4j.create(array, 1, nofFeatures);
    }

    @Override
    public String toString() {
        List<String> discreteVariableKeyValuePairs = new ArrayList<>();
        List<String> continuousVariableKeyValuePairs = new ArrayList<>();

        for (String name : discreteVariables.keySet())
            discreteVariableKeyValuePairs.add(name + "=" + discreteVariables.get(name));

        for (String name : continuousVariables.keySet())
            continuousVariableKeyValuePairs.add(name + "=" + continuousVariables.get(name));

        return '{'+String.join(",", discreteVariableKeyValuePairs)+
                String.join(",", continuousVariableKeyValuePairs)+'}';
    }
}
