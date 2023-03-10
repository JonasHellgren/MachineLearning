package mcts_elevator;

import common.RandUtils;
import monte_carlo_tree_search.domains.elevator.ElevatorStateValueMemory;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestElevatorStateValueMemory {

    private static final int MINI_BATCH_SIZE = 10;
    private static final double DELTA = 1;
    private static final double MIN_OUT = 0;
    private static final double MAX_OUT = 10;
    ElevatorStateValueMemory<VariablesElevator> memory;
    @Before
    public void init() {
        memory=new ElevatorStateValueMemory<>(MIN_OUT, MAX_OUT);
    }

    @Test
    public void  givenExperienceSoCEqualValue_whenLearn_thenCorrect() {
        List<Experience<VariablesElevator, Integer>> miniBatch = createMiniBatch();
        for (int i = 0; i <1000 ; i++) {
            memory.learn(miniBatch);
        }

        System.out.println("memory.read(getState(0.2)) = " + memory.read(getState(0.2)));

        assertAll(
                () -> assertEquals(3, memory.read(getState(0.3)), DELTA),
                () -> assertEquals(6, memory.read(getState(0.6)),DELTA),
                () -> assertEquals(9, memory.read(getState(0.9)),DELTA)
        );
    }

    @NotNull
    private List<Experience<VariablesElevator, Integer>> createMiniBatch() {
        List<Experience<VariablesElevator, Integer>> miniBatch=new ArrayList<>();
        for (int i = 0; i < MINI_BATCH_SIZE; i++) {
            double randNum=RandUtils.getRandomDouble(MIN_OUT,MAX_OUT);
            miniBatch.add(createExperience(randNum/MAX_OUT,randNum));

        }
        return miniBatch;
    }

    @NotNull
    private StateInterface<VariablesElevator> getState(double SoE) {
        return StateElevator.newFromVariables(VariablesElevator.builder().SoE(SoE).build());
    }

    private Experience<VariablesElevator, Integer> createExperience(double SoE, double value) {
        System.out.println("SoE = " + SoE);
        System.out.println("value = " + value);
        return Experience.<VariablesElevator, Integer>builder()
                .stateVariables(VariablesElevator.builder().SoE(SoE).build())
                .value(value)
                .build();
    }


}
