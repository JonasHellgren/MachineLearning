package safe_rl.domain.environment.aggregates;

public interface StateI<V> {
    V getVariables();
    void setVariables(V variables);
    StateI<V> copy();
    double[] continuousFeatures();
    int nContinuousFeatures();
    int[] discreteFeatures();
    void setContinuousFeatures(double[] features);
    void setDiscreteFeatures(int[] features);
    int hashCode();
    boolean equals(Object o);

}
