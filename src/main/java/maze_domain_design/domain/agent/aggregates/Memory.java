package maze_domain_design.domain.agent.aggregates;

import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.agent.value_objects.StateAction;
import maze_domain_design.domain.environment.value_objects.State;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Memory {

    Map<StateAction, Double> mapValue;
    AgentProperties properties;

    public Memory(AgentProperties properties) {
        this.mapValue=new HashMap<>();
        this.properties=properties;
    }

    public static Memory withProperties(AgentProperties properties) {
        return new Memory(properties);
    }

    public Double read(StateAction sa) {
        return mapValue.containsKey(sa)
                ? mapValue.get(sa)
                : properties.defaultValue();
    }

    public void fit(StateAction sa, double valueTar) {
        var valOld= read(sa);
        write(sa,valOld+properties.learningRate()*(valueTar-valOld));
    }

    public void write(StateAction sa, double value) {
        mapValue.put(sa,value);
    }


    public double valueOfState(State s) {
        var aValueMap = Stream.of(properties.actions())
                .collect(Collectors.toMap(
                        a -> a,
                        a -> read(StateAction.of(s, a))));
        var entryWithMaxValue = aValueMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        return entryWithMaxValue.orElseThrow().getValue();
    }
}
