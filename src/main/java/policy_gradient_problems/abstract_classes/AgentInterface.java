package policy_gradient_problems.abstract_classes;

public interface AgentInterface<V> {
     int chooseActionOld();

     Action chooseAction();
     void setState(StateInterface<V> state);

}
