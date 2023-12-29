package policy_gradient_problems.abstract_classes;

public interface AgentI<V> {

     Action chooseAction();
     StateI<V>  getState();
     void setState(StateI<V> state);

}
