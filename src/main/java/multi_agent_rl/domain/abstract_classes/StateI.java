package multi_agent_rl.domain.abstract_classes;

public interface StateI<V,O> {
    V getVariables();
    void setVariables(V variables);
    StateI<V,O> copy();

     ObservationI<O> getObservation(String id);

/*
    double[] continuousFeatures();
    int nContinuousFeatures();
    int[] discreteFeatures();
    void setContinuousFeatures(double[] features);
    void setDiscreteFeatures(int[] features);
    int hashCode();
    boolean equals(Object o);
*/

}
