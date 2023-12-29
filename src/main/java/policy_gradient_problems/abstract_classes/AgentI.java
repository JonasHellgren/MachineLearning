package policy_gradient_problems.abstract_classes;

public interface AgentI<V> {

     Action chooseAction();
     void setState(StateI<V> state);

}
