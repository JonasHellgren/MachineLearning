package maze_domain_design;

import common.other.Conditionals;
import lombok.extern.java.Log;
import maze_domain_design.domain.agent.aggregates.Memory;
import maze_domain_design.domain.agent.value_objects.AgentProperties;
import maze_domain_design.domain.agent.value_objects.StateAction;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.EnvironmentProperties;
import maze_domain_design.domain.environment.value_objects.State;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

@Log
public class TestMemory {

    public static final int MAX_VALUE = 100;
    public static final int VALUE_TAR = 1;
    public static final double TOL = 1e-2;
    Memory memory;
    AgentProperties properties;
    EnvironmentProperties envProps;

    @BeforeEach
    void init() {
        properties = AgentProperties.roadMaze();
        envProps=EnvironmentProperties.roadMaze();
        memory=Memory.withProperties(properties);
    }

    @Test
    void whenNotDefined_thenDefaultValue() {
        var sa=getRandomSa();
        System.out.println("sa = " + sa);
        Assertions.assertEquals(properties.defaultValue(),memory.read(sa));
    }

    @Test
    void whenWriteAndRead_thenCorrect() {
        IntStream.range(0,10_000).forEach((n) -> set2RandomAndAssert());
    }

    @Test
    void whenFitting_thenCorrect() {
        var sa=getRandomSa();
        IntStream.range(0,100).forEach((n) -> memory.fit(sa, VALUE_TAR));
        Assertions.assertEquals(VALUE_TAR,memory.read(sa), TOL);

    }

    private void set2RandomAndAssert() {
        double r0= RandomUtils.nextDouble(0, MAX_VALUE);
        double r1= RandomUtils.nextDouble(0, MAX_VALUE);
        var sa0=getRandomSa();
        var sa1=getRandomSa();
        boolean equal=sa0.equals(sa1);
        memory.write(sa0,r0);
        memory.write(sa1,r1);

        Conditionals.executeOneOfTwo(equal,
                () -> Assertions.assertEquals(memory.read(sa0),memory.read(sa1)),
                () -> Assertions.assertNotEquals(memory.read(sa0),memory.read(sa1)));
    }


    StateAction getRandomSa() {
        var s= State.ofRandom(envProps);
        var a= Action.random();
        return new StateAction(s,a);
    }


}