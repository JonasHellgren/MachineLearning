package policy_gradient_problems.abstract_classes;

import org.apache.commons.math3.linear.RealVector;

public interface AgentThetaActorInterface {
    void changeActor(RealVector change);
}
