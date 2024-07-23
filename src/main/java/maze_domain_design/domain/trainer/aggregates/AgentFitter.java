package maze_domain_design.domain.trainer.aggregates;

import lombok.AllArgsConstructor;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.agent.value_objects.StateAction;
import maze_domain_design.domain.trainer.entities.Experience;

/***
 *  qSa <- qSa+learningRate*(qSaTar-qSa)
 *  where v(s')=qSa(s',aBest) and qSaTar=r+gamma*v(s')
 */

@AllArgsConstructor
public class AgentFitter {
    MediatorI mediator;

    public double fitAgentFromExperience(Experience e) {
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
