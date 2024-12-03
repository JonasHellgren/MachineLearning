package domain_design_tabular_q_learning.domain.agent.helpers;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.domain.agent.aggregates.Memory;
import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StepReturn;
import domain_design_tabular_q_learning.domain.trainer.entities.Experience;
import domain_design_tabular_q_learning.domain.trainer.value_objects.SARS;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * tuple={sInit,ai,ri,si}, si is new stateNew from transition T(sInit,a)
 * Qsa i=   |r(sInit,ai)                (si isTerminal)
 *          |r(sInit,ai)+gammas*V(si)    (else)
 * where V(si)=max a Q(si,a)
 */

@AllArgsConstructor
public class BestActionSelector<V,A,P> {
    AgentProperties properties;
    EnvironmentI<V,A,P> environment;
    Memory<V,A> memory;

    public ActionI<A> chooseBestAction(StateI<V> s) {
        var expList = getExperienceList(s);
        var aQsaMap = getActionQsaMap(expList);
        var entry = getEntryWithMaxValue(aQsaMap);
        return entry.orElseThrow().getKey();
    }

    Optional<Map.Entry<ActionI<A>, Double>> getEntryWithMaxValue(
            Map<ActionI<A>, Double> aQsaMap) {
        return aQsaMap.entrySet().stream()
                .max(Map.Entry.comparingByValue());
    }

    Map<ActionI<A>, Double> getActionQsaMap(List<Experience<V,A>> expList) {
        return expList.stream().collect(Collectors.toMap(
                e -> e.getSars().a(),
                e -> {
                    SARS<V,A> sars = e.getSars();
                    return e.getType().isTerminal()
                            ? sars.r()
                            : sars.r() + properties.gamma() *
                            memory.valueOfState(sars.sNext());
                }));
    }

    List<Experience<V,A>> getExperienceList(StateI<V> s) {
        return Stream.of(environment.actions()).map(a -> {
            StepReturn<V> sr = environment.step(s, a);
            return Experience.ofIdStateActionStepReturn(a.ordinal(),s, a, sr);
        }).toList();
    }

}
