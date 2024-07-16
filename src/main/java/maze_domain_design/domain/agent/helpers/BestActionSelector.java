package maze_domain_design.domain.agent.helpers;

import lombok.AllArgsConstructor;
import maze_domain_design.domain.agent.aggregates.Memory;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.domain.environment.value_objects.StepReturn;
import maze_domain_design.domain.trainer.entities.Experience;
import maze_domain_design.domain.trainer.value_objects.SARS;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * tuple={sInit,ai,ri,si}, si is new state from transition T(sInit,a)
 * Qsa i=   |r(sInit,ai)                (si isTerminal)
 *          |r(sInit,ai)+gamma*V(si)    (else)
 * where V(si)=max a Q(si,a)
 */

@AllArgsConstructor
public class BestActionSelector {
    AgentProperties properties;
    Environment environment;
    Memory memory;

    public Action chooseBestAction(State s) {
        var expList = getExperienceList(s);
        var aQsaMap = getActionQsaMap(expList);
        var entry = getEntryWithMaxValue(aQsaMap);
        return entry.orElseThrow().getKey();
    }

    Optional<Map.Entry<Action, Double>> getEntryWithMaxValue(
            Map<Action, Double> aQsaMap) {
        return aQsaMap.entrySet().stream()
                .max(Map.Entry.comparingByValue());
    }

    Map<Action, Double> getActionQsaMap(List<Experience> expList) {
        return expList.stream().collect(Collectors.toMap(
                e -> e.getSars().a(),
                e -> {
                    SARS sars = e.getSars();
                    return e.getType().isTerminal()
                            ? sars.r()
                            : sars.r() + properties.gamma() *
                            memory.valueOfState(sars.sNext());
                }));
    }

    List<Experience> getExperienceList(State s) {
        return Stream.of(properties.actions()).map(a -> {
            StepReturn sr = environment.step(s, a);
            return Experience.ofIdStateActionStepReturn(a.ordinal(),s, a, sr);
        }).toList();
    }

}