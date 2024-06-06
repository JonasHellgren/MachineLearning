package multi_agent_rl.domain.abstract_classes;

public interface ObservationI<O> {
    O getVariables();
    void setVariables(O variables);
    ObservationI<O> copy();

}
