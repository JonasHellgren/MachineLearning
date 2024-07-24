package domain_design_tabular_q_learning.domain.agent.aggregates;

import domain_design_tabular_q_learning.domain.agent.value_objects.AgentProperties;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Memory<S,A> {

    Map<StateAction<S,A>, Double> mapValue;
    AgentProperties properties;
    ActionI<A>[] actionArr;

    public Memory(AgentProperties properties,ActionI<A>[] actionArr) {
        this.mapValue = new HashMap<>();
        this.properties = properties;
        this.actionArr=actionArr;
    }

    public static <S,A> Memory<S,A> of(AgentProperties properties, ActionI<A>[] actionArr) {
        return new Memory<>(properties,actionArr);
    }

    public Double read(StateAction<S,A> sa) {
        return mapValue.containsKey(sa)
                ? mapValue.get(sa)
                : properties.defaultValue();
    }

    public double fit(StateAction<S,A> sa, double valueTar) {
        var valOld = read(sa);
        double err = valueTar - valOld;
        write(sa, valOld + properties.learningRate() * err);
        return err;
    }

    public void write(StateAction<S,A> sa, double value) {
        mapValue.put(sa, value);
    }

    public double valueOfState(StateI<S> s) {
        var aValueMap = Stream.of(actionArr)
                .collect(Collectors.toMap(
                        a -> a,
                        a -> read(StateAction.of(s, a))));
        var entryWithMaxValue = aValueMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        return entryWithMaxValue.orElseThrow().getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        mapValue.forEach((key, value) ->
                sb.append("sa=").append(key)
                        .append(", value=").append(value).append(System.lineSeparator()));
        return sb.toString();
    }
}
