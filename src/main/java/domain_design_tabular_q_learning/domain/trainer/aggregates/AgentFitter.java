package domain_design_tabular_q_learning.domain.trainer.aggregates;

import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.domain.trainer.entities.Experience;

/***
 *  qSa <- qSa+learningRate*(qSaTar-qSa)
 *  where v(s')=qSa(s',aBest) and qSaTar=r+gamma*v(s')
 */

@AllArgsConstructor
public class AgentFitter<V,A,P> {
    MediatorI<V,A,P> mediator;

    public double fitAgentFromExperience(Experience<V,A> e) {
        var agent = mediator.getExternal().agent();
        var memory= agent.getMemory();
        var sa = StateAction.ofSars(e);
        var pa=agent.getProperties();
        double r=e.getSars().r();
        double v=memory.valueOfState(e.getSars().sNext());
        double qSaTar=r+pa.gamma()*v;
        return agent.getMemory().fit(sa,qSaTar);
    }
}
