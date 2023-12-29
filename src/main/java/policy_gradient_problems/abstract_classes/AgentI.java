package policy_gradient_problems.abstract_classes;

public interface AgentI<V> {
     int chooseActionOld();

     Action chooseAction();
     void setState(StateI<V> state);

}
