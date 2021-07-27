package qlearning_objoriented_class_sceleton.models;

import java.util.ArrayList;
import java.util.List;

public abstract class EnvironmentParametersAbstract {
    public List<String> discreteStateVariableNames=new ArrayList<>();
    public List<String> continuousStateVariableNames=new ArrayList<>();
    public List<Integer> discreteActionsSpace=new ArrayList<>();

}

